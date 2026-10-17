package cz.ecis.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.querydsl.core.types.Predicate;

import cz.ecis.core.exception.DocumentException;
import cz.ecis.core.exception.EcisRuntimeException;
import cz.ecis.core.security.CampSecurityContext;
import cz.ecis.core.settings.EcisStorageSettings;
import cz.ecis.criteria.ApplicationOverviewVieCriteria;
import cz.ecis.db.ent.ApplicationDocumentEnt;
import cz.ecis.db.ent.ApplicationEnt;
import cz.ecis.db.ent.ApplicationOverviewVieEnt;
import cz.ecis.db.ent.ApplicationParentEnt;
import cz.ecis.db.ent.CampEnt;
import cz.ecis.db.ent.ChildEnt;
import cz.ecis.db.ent.LkpDocumentTypeEnt;
import cz.ecis.db.ent.ParentEnt;
import cz.ecis.db.repo.ApplicationDocumentRepository;
import cz.ecis.db.repo.ApplicationOverviewVieRepository;
import cz.ecis.db.repo.ApplicationRepository;
import cz.ecis.db.repo.LkpDocumentTypeRepository;
import cz.ecis.mapper.ApplicationMapper;
import cz.ecis.model.dto.ApplicationCreateDto;
import cz.ecis.model.dto.ApplicationDocumentCreateDto;
import cz.ecis.model.dto.ApplicationDto;
import cz.ecis.model.dto.ApplicationOverviewDto;
import cz.ecis.model.dto.ChildFilterDto;
import cz.ecis.model.dto.ParentDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final EcisStorageSettings ecisStorageSettings;

    private final ApplicationMapper applicationMapper;
    private final ChildService childService;
    private final ParentService parentService;
    private final ApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;
    private final LkpDocumentTypeRepository lkpDocumentTypeRepository;
    private final ApplicationOverviewVieRepository applicationOverviewVieRepository;

    public List<ApplicationOverviewDto> getAllApplications() {
        return this.applicationOverviewVieRepository.findAll().stream()
            .map(this.applicationMapper::toOverviewDto)
            .toList();
    }

    public List<ApplicationOverviewDto> search(@Nullable ChildFilterDto filter) {
        Long camp = CampSecurityContext.getCampId();
            Predicate criteria = ApplicationOverviewVieCriteria.byChildFilter(filter, camp);
            return this.applicationOverviewVieRepository.findAll(criteria).stream()
                .map(this.applicationMapper::toOverviewDto)
                .toList();
    }

    public ApplicationDto createApplication(ApplicationCreateDto dto) {
        ApplicationEnt ent = createApplicationEnt(dto);
        return applicationMapper.toDto(ent);
    }

    @Transactional
    public ApplicationDto getApplicationById(Long id) {
        return this.applicationMapper.toDto(this.getById(id));
    }

    @Transactional
    public ApplicationContentResult getApplicationDocumentResult(Long id, UUID documentId) {
        Long campId = CampSecurityContext.getCampId();
        ApplicationDocumentEnt document = this.applicationDocumentRepository.findByIdAndApplicationIdAndCampId(documentId, id, campId);
        byte[] data = {};
        try {
            data = Files.readAllBytes(Path.of(document.getFileLocation()));
        } catch (IOException | OutOfMemoryError e) {
            LOGGER.error("Error during file read", e);
            throw new DocumentException("Error during document read");
        }
        return new ApplicationContentResult(document.getFilename(), data);
    };

    @Transactional
    public ApplicationOverviewDto cancelApplication(Long id) {
        ApplicationEnt ent = this.getById(id);

        if (ent.getCanceledAt() != null) {
            throw new EcisRuntimeException("Application is already canceled", HttpStatus.BAD_REQUEST);
        }

        ent.setConfirmedAt(null);
        ent.setCanceledAt(OffsetDateTime.now());

        this.applicationRepository.save(ent);
        return this.applicationMapper.toOverviewDto(ent);
    }

    @Transactional
    public ApplicationOverviewDto confirmApplication(Long id) {
        ApplicationEnt ent = this.getById(id);

        if (ent.getConfirmedAt() != null) {
            throw new EcisRuntimeException("Application is already canceled", HttpStatus.BAD_REQUEST);
        }

        ent.setConfirmedAt(OffsetDateTime.now());
        ent.setCanceledAt(null);

        this.applicationRepository.save(ent);
        return this.applicationMapper.toOverviewDto(ent);
    }

    private ApplicationEnt getById(Long id) {
        Long campId = CampSecurityContext.getCampId();
        return this.applicationRepository.findByIdAndCampId(id, campId);
    }

    @Transactional
    private ApplicationEnt createApplicationEnt(ApplicationCreateDto createDto) {

        CampEnt camp = CampSecurityContext.getCamp();
        ChildEnt child = childService.findChildOrCreate(createDto.getChild());

        ApplicationEnt ent = applicationMapper.create(createDto);
        ent.setCamp(camp);
        ent.setChild(child);

        for (ParentDto parentDto : createDto.getParents()) {
            ParentEnt parent = parentService.findParentOrCreate(parentDto);

            ApplicationParentEnt relation = new ApplicationParentEnt();
            relation.setApplication(ent);
            relation.setParent(parent);

            ent.getParents().add(relation);
        }

        ent = applicationRepository.save(ent);

        List<ApplicationDocumentEnt> documents = new ArrayList<>(createDto.getDocuments().size());
        for (ApplicationDocumentCreateDto dto : createDto.getDocuments()) {
            try {
                documents.add(this.createDocument(dto, ent));
            } catch (IOException e) {
                LOGGER.error("Error during creating document {}", dto.getFilename(), e);
                throw new DocumentException("Error during creating document: " + dto.getFilename());
            }
        }
        documents = applicationDocumentRepository.saveAll(documents);

        ent.setDocuments(documents);

        return ent;
    }

    private ApplicationDocumentEnt createDocument(ApplicationDocumentCreateDto dto, ApplicationEnt application) throws IOException {
        byte[] file = dto.getData();
        String extension = FilenameUtils.getExtension(dto.getFilename());
        LkpDocumentTypeEnt type = this.lkpDocumentTypeRepository.findByExtensionIgnoreCaseValid(extension);
        String generatedFilename = UUID.randomUUID().toString();
        Path fullPath = Path.of(this.ecisStorageSettings.directory(), generatedFilename);

        ApplicationDocumentEnt ent = new ApplicationDocumentEnt();
        ent.setDocumentType(type);
        ent.setFileLocation(fullPath.toString());
        ent.setFilename(dto.getFilename());
        ent.setApplication(application);

        String fileSize = null;
        try (InputStream inputStream = new ByteArrayInputStream(file)) {
            fileSize = FileUtils.byteCountToDisplaySize(file.length);
            Files.createDirectories(fullPath.getParent());
            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Error during proccessing file " + dto.getFilename(), e);
        }
        ent.setFileSize(fileSize);

        return ent;
    }

    public record ApplicationContentResult(String filename, byte[] data) {}
}
