package cz.ecis.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.security.annotation.CampSecured;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;
import cz.ecis.model.dto.RefreshTokenRequestDto;
import cz.ecis.model.dto.TwoFaCodeDto;
import cz.ecis.model.dto.TwoFaQrCodeDto;
import cz.ecis.model.dto.UserCredentialsDto;
import cz.ecis.service.SecService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
public class SecController implements SecApiInterface {

    private final SecService secService;

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto request) {
        LoginResponseDto response =  this.secService.getLoginResponse(request);
        if (Boolean.TRUE.equals(response.getTwoFaRequired())) {
            return ResponseEntity.accepted().body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<Void> logout() {
        this.secService.logout();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserCredentialsDto> getCredentials() {
        return ResponseEntity.ok(
            this.secService.getCredentials()
        );
    }

    @Override
    @CampSecured
    public ResponseEntity<List<String>> getRoles() {
        return ResponseEntity.ok(
            this.secService.getRoles()
        );
    }

    @Override
    public ResponseEntity<TwoFaQrCodeDto> getTwoFaQ() {
        return ResponseEntity.ok(
            this.secService.getTwoFaQ()
        );
    }

    @Override
    public ResponseEntity<Void> enableTwoFaQ(TwoFaCodeDto dto) {
        this.secService.enableTwoFaQ(dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LoginResponseDto> refreshToken(RefreshTokenRequestDto dto) {
        return ResponseEntity.ok(
            this.secService.refreshToken(dto)
        );
    }

}
