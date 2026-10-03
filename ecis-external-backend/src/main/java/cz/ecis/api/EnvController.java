package cz.ecis.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.environment.EnvTypeEnum;
import cz.ecis.model.dto.EnvDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/ext/v1")
@RequiredArgsConstructor
public class EnvController implements EnvApiInterface {

    @Override
    public ResponseEntity<EnvDto> getAppEnv() {
        EnvDto dto = new EnvDto("1", EnvTypeEnum.DEV);
        return ResponseEntity.ok(dto);
    }
    
}
