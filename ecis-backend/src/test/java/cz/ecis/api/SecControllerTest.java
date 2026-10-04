package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;
import cz.ecis.model.dto.RefreshTokenRequestDto;
import cz.ecis.model.dto.TwoFaCodeDto;
import cz.ecis.model.dto.TwoFaQrCodeDto;
import cz.ecis.model.dto.UserCredentialsDto;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import tools.jackson.core.type.TypeReference;

class SecControllerTest extends EcisIntegrationTestClient {

    @Test
    @Order(1)
    void testLogin() throws Exception {

        LoginResponseDto response = this.runPost(
            SecController.PATH_LOGIN,
            "test",
            new LoginRequestDto("test", "test"),
            LoginResponseDto.class
        );

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getTwoFaRequired()).isFalse();
    }

    @Test
    @Order(2)
    void testGetCredentials() throws Exception {

        UserCredentialsDto credentials = this.runGet(
            SecController.PATH_GET_CREDENTIALS,
            "test",
            UserCredentialsDto.class
        );

        assertThat(credentials.getUsername()).isEqualTo("test");
    }

    @Test
    @Order(3)
    void testGetRoles() throws Exception {

        List<String> roles = this.withHeader("X-Camp-Id", "1")
            .runGet(
                SecController.PATH_GET_ROLES,
                "test",
                new TypeReference<List<String>>() {}
            );

        assertThat(roles).contains("00000");
    }

    @Test
    @Order(4)
    void testGetTwoFaQ() throws Exception {

        TwoFaQrCodeDto qrCode = this.runGet(
            SecController.PATH_GET_TWO_FA_Q,
            "test",
            TwoFaQrCodeDto.class
        );

        assertThat(qrCode.getQrUrl()).startsWith("otpauth://totp/");
    }

    @Test
    @Order(5)
    void testRefreshToken() throws Exception {

        LoginResponseDto login = this.runPost(
            SecController.PATH_LOGIN,
            "test",
            new LoginRequestDto("test", "test"),
            LoginResponseDto.class
        );
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken(login.getRefreshToken());
        this.withUserToken("refresh", "Bearer " + login.getToken());

        LoginResponseDto response = this.runPost(
            SecController.PATH_REFRESH_TOKEN,
            "refresh",
            request,
            LoginResponseDto.class
        );

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getTwoFaRequired()).isFalse();
    }

    @Test
    @Order(6)
    void testEnableTwoFaAndLogin() throws Exception {

        String code = null;
        for (int attempt = 0; attempt < 20; attempt++) {
            TwoFaQrCodeDto qrCode = this.runGet(
                SecController.PATH_GET_TWO_FA_Q,
                "test",
                TwoFaQrCodeDto.class
            );
            String secret = Arrays.stream(URI.create(qrCode.getQrUrl()).getRawQuery().split("&"))
                .filter(parameter -> parameter.startsWith("secret="))
                .map(parameter -> parameter.substring("secret=".length()))
                .findFirst()
                .orElseThrow();
            code = new DefaultCodeGenerator().generate(
                secret,
                new SystemTimeProvider().getTime() / 30
            );
            if (code.length() == 6 && code.charAt(0) != '0') {
                break;
            }
        }
        assertThat(code).hasSize(6);
        TwoFaCodeDto twoFaCode = new TwoFaCodeDto();
        twoFaCode.setTwoFaCode(Integer.valueOf(code));

        this.withExpectedEmptyContent()
        .runPatch(SecController.PATH_ENABLE_TWO_FA_Q, "test", twoFaCode, Object.class);

        LoginRequestDto loginRequest = new LoginRequestDto("test", "test");

        LoginResponseDto loginResponse = this.withExpectedStatus(HttpStatus.ACCEPTED)
        .runPost(SecController.PATH_LOGIN, "test", loginRequest, LoginResponseDto.class);

        assertThat(loginResponse.getToken()).isNull();
        assertThat(loginResponse.getTwoFaRequired()).isTrue();

        loginRequest.setTwoFaCode(Integer.valueOf(code));
        loginResponse = runPost(SecController.PATH_LOGIN, "test", loginRequest, LoginResponseDto.class);

        assertThat(loginResponse.getToken()).isNotBlank();
        assertThat(loginResponse.getTwoFaRequired()).isFalse();
    }

    @Test
    @Order(7)
    void testLogout() throws Exception {

        Object result = this.withExpectedEmptyContent()
            .runPost(SecController.PATH_LOGOUT, "test", null, Object.class);

        assertThat(result).isNull();
    }
}