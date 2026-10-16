package cz.ecis.api;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.security.EcisRoleEnum;
import cz.ecis.core.security.annotation.CampSecured;
import cz.ecis.model.dto.ApplicationCreateDto;
import cz.ecis.model.dto.ApplicationDto;
import cz.ecis.model.dto.ApplicationOverviewDto;
import cz.ecis.model.dto.ChildFilterDto;
import cz.ecis.service.ApplicationService;
import cz.ecis.service.ApplicationService.ApplicationContentResult;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApiInterface {

    private final ApplicationService applicationService;

    @Override
    @CampSecured
    public ResponseEntity<List<ApplicationOverviewDto>> getAllApplications() {
        return ResponseEntity.ok(
            this.applicationService.getAllApplications()
        );
    }

    @Override
    @CampSecured(roles = {EcisRoleEnum.CHILDREN_READ})
    public ResponseEntity<List<ApplicationOverviewDto>> searchApplications(@Nullable ChildFilterDto filter) {
        return ResponseEntity.ok(this.applicationService.search(filter));
    }

    @Override
    @CampSecured
    public ResponseEntity<ApplicationDto> getApplicationById(Long id) {
        return ResponseEntity.ok(
            this.applicationService.getApplicationById(id)
        );
    }

    @Override
    @CampSecured
    public ResponseEntity<ApplicationDto> createApplication(ApplicationCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            this.applicationService.createApplication(dto)
        );
    }

    @Override
    public ResponseEntity<Resource> getApplicationDocumentContentById(Long id, UUID documentId) {
        ApplicationContentResult result = this.applicationService.getApplicationDocumentResult(id, documentId);

        MediaType mediaType = MediaTypeFactory.getMediaType(result.filename())
            .orElse(MediaType.APPLICATION_OCTET_STREAM);

        String contentDisposition = ContentDisposition.attachment()
            .filename(result.filename(), StandardCharsets.UTF_8)
            .build()
            .toString();

        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .contentLength(result.data().length)
            .body(new ByteArrayResource(result.data()));

    }
}
