package cz.ecis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.exception.IllegalRecordStateException;
import cz.ecis.core.mapper.LovDtoMapper;
import cz.ecis.core.model.LovDto;
import cz.ecis.core.security.EcisUserDetailsService;
import cz.ecis.db.ent.CampEnt;
import cz.ecis.db.repo.CampRepository;
import cz.ecis.mapper.CampMapper;
import cz.ecis.model.dto.CampDto;
import cz.ecis.utils.CrudUtils;
import cz.ecis.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampService {

    private final CampRepository campRepository;
    private final CampMapper campMapper;
    private final LovDtoMapper lovDtoMapper;

    private final EcisUserDetailsService ecisUserDetailsService;

    public List<CampDto> getAll() {
        return SecurityUtils.getUserCamps().stream()
            .map(this.campMapper::toDto)
            .toList();
    }

    public List<LovDto> getAllLov() {
        return SecurityUtils.getUserCamps().stream()
            .map(this.lovDtoMapper::toLovDto)
            .toList();
    }

    public CampDto create(CampDto dto) {
        if (dto.getDateTo() != null && dto.getDateFrom().isAfter(dto.getDateTo())) {
            throw new IllegalRecordStateException("Dto check failed", "Date from must be before or equals to date to");
        }

        CampEnt camp = this.createCamp(dto);

        this.ecisUserDetailsService.assignUserToCamp(camp);

        return this.campMapper.toDto(camp);
    }

    public CampDto updateById(Long id, CampDto dto) {
        CrudUtils.checkIdViolation(id, dto.getId());
        CampEnt ent = SecurityUtils.getUserCampById(id);

        return this.campMapper.toDto(
            this.updateCamp(ent, dto)
        );
    }

    @Transactional
    public CampDto userFindById(Long id) {
        return campMapper.toDto(
            SecurityUtils.getUserCamps().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotExistsException("Camp with given id not found", id))
        );
    }

    @Transactional
    public CampEnt findById(Long id) {
        return this.campRepository.findById(id)
            .orElseThrow(() -> new EntityNotExistsException("Camp with given id not found", id));
    }

    @Transactional
    private CampEnt updateCamp(CampEnt ent, CampDto dto) {
        CrudUtils.checkEntityVersion(ent.getVersion(), dto.getVersion());
        this.campMapper.update(ent, dto);
        return this.campRepository.save(ent);
    }

    @Transactional
    private CampEnt createCamp(CampDto dto) {
        return this.campRepository.save(
            this.campMapper.create(dto)
        );
    }

}
