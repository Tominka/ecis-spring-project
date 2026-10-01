package cz.ecis.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.model.dto.EnvDto;
import cz.ecis.service.EnvService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class EnvController implements EnvApiInterface {
    
    private final EnvService envService;

    @Override
    public ResponseEntity<EnvDto> getAppEnv() {
        return ResponseEntity.ok(this.envService.getEnvDto());
    }
    
}
