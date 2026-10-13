package cz.ecis.service;

import cz.ecis.core.security.EcisUserDetailsService;
import java.util.List;
import org.springframework.stereotype.Service;

import cz.ecis.core.mapper.LovDtoMapper;
import cz.ecis.core.model.LovDto;
import cz.ecis.mapper.CampMapper;
import cz.ecis.model.dto.CampDto;
import cz.ecis.model.dto.UserCampChangeDto;
import cz.ecis.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EcisUserDetailsService ecisUserDetailsService;

    private final CampMapper campMapper;
    private final LovDtoMapper lovDtoMapper;

    public List<CampDto> getUserCamps() {
        return SecurityUtils.getUserCamps().stream()
            .map(this.campMapper::toDto)
            .toList();
    }

    public List<LovDto> getUserCampsLov() {
        return SecurityUtils.getUserCamps().stream()
            .map(this.lovDtoMapper::toLovDto)
            .toList();
    }

    @Transactional
    public void selectCamp(UserCampChangeDto dto) {
        this.ecisUserDetailsService.selectUserCamp(dto.getId());
    }
}