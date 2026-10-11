package cz.ecis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cz.ecis.core.mapper.LovDtoMapper;
import cz.ecis.core.model.LovDto;
import cz.ecis.core.security.CampSecurityContext;
import cz.ecis.db.ent.ChildEnt;
import cz.ecis.db.repo.ChildRepository;
import cz.ecis.mapper.ChildMapper;
import cz.ecis.model.dto.ChildDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final ChildRepository childRepository;
    private final ChildMapper childMapper;
    private final LovDtoMapper lovDtoMapper;

    public List<ChildDto> getAll() {
        Long camp = CampSecurityContext.getCampId();

        return this.childRepository.findAllByApplicationsCampId(camp).stream()
            .map(this.childMapper::toDto)
            .toList();
    }

    public List<LovDto> getAllLov() {
        Long camp = CampSecurityContext.getCampId();

        return this.childRepository.findAllByApplicationsCampId(camp).stream()
            .map(this.lovDtoMapper::toLovDto)
            .toList();
    }

    public ChildEnt findChildOrCreate(ChildDto dto) {
        return this.childRepository.findByNameAndSurnameAndBirthdateAndAddress(
            dto.getName(), dto.getSurname(), dto.getBirthdate(), dto.getAddress()
        ).orElseGet(() -> {
            ChildEnt newChild = this.childMapper.create(dto);
            return this.childRepository.save(newChild);
        });
    }

    public record ChildCreateResult(ChildDto child, boolean found) {}
}
