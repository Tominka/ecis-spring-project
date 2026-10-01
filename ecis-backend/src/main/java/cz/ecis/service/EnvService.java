package cz.ecis.service;

import org.springframework.stereotype.Service;

import cz.ecis.core.EnvironmentContext;
import cz.ecis.mapper.EnvMapper;
import cz.ecis.model.dto.EnvDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnvService {
    
    private final EnvironmentContext environmentContext;
    private final EnvMapper envMapper;

    public EnvDto getEnvDto() {
        return envMapper.mapContextToDto(
            environmentContext.getAppContext()
        );
    }
}
