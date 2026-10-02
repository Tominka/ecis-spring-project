package cz.ecis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cz.ecis.core.model.LovDto;
import cz.ecis.db.repo.LkpChildInfoRepository;
import cz.ecis.mapper.LookupMapper;
import cz.ecis.model.dto.LookupBaseDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LkpChildInfoService {

    private final LkpChildInfoRepository lkpChildInfoRepository;
    private final LookupMapper lookupBaseDtoMapper;

    public List<LookupBaseDto> getAll() {
        return this.lkpChildInfoRepository.findAll().stream()
            .map(this.lookupBaseDtoMapper::toDto)
        .toList();
    }

    public List<LovDto> getAllLov() {
        return this.lkpChildInfoRepository.findAll().stream()
            .map(this.lookupBaseDtoMapper::toLovDto)
        .toList();
    }
}