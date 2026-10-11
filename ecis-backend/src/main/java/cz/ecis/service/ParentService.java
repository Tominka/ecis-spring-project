package cz.ecis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cz.ecis.core.mapper.LovDtoMapper;
import cz.ecis.core.model.LovDto;
import cz.ecis.core.security.CampSecurityContext;
import cz.ecis.db.ent.ParentEnt;
import cz.ecis.db.repo.ParentRepository;
import cz.ecis.mapper.ParentMapper;
import cz.ecis.model.dto.ParentDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final ParentMapper parentMapper;
    private final LovDtoMapper lovDtoMapper;

    public List<ParentDto> getAll() {
        Long camp = CampSecurityContext.getCampId();

        return this.parentRepository.findAllByCampId(camp).stream()
            .map(this.parentMapper::toDto)
            .toList();
    }

    public List<LovDto> getAllLov() {
        Long camp = CampSecurityContext.getCampId();

        return this.parentRepository.findAllByCampId(camp).stream()
            .map(this.lovDtoMapper::toLovDto)
            .toList();
    }

    public ParentEnt findParentOrCreate(ParentDto dto) {
        return this.parentRepository.findByNameAndSurnameAndAddress(
            dto.getName(), dto.getSurname(), dto.getAddress()
        ).orElseGet(() -> {
            ParentEnt newParent = this.parentMapper.create(dto);
            return this.parentRepository.save(newParent);
        });
    }

}
