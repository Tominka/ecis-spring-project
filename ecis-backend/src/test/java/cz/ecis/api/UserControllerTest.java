package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.core.model.LovDto;
import cz.ecis.db.repo.UserRepository;
import cz.ecis.model.dto.CampDto;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;
import cz.ecis.model.dto.UserCampChangeDto;
import cz.ecis.model.dto.UserSettingsDto;
import tools.jackson.core.type.TypeReference;


class UserControllerTest extends EcisIntegrationTestClient {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    void testGetAllCamps() throws Exception {

        List<CampDto> camps = this.runGet(UserController.PATH_GET_USER_CAMPS, "test", new TypeReference<List<CampDto>>() {});

        assertThat(camps).hasSize(1);
        assertThat(camps.get(0).getId()).isEqualTo(1);
    }

    @Test
    @Order(2)
    void testGetAllCampsLov() throws Exception {

        List<LovDto> camps = this.runGet(UserController.PATH_GET_USER_CAMPS_LOV, "test", new TypeReference<List<LovDto>>() {});

        assertThat(camps).hasSize(1);
        assertThat(camps.get(0).getId()).isEqualTo(1);
    }

    @Test
    @Order(3)
    void testUpdateOwnSettingsReturnsUpdatedSettings() throws Exception {
        UserSettingsDto request = new UserSettingsDto("/updated", true, false);
        this.addUrlParam("id", 1L);

        UserSettingsDto updated = this.runPut(
            UserController.PATH_UPDATE_USER_SETTINGS,
            "test",
            request,
            UserSettingsDto.class
        );

        assertThat(updated.getDefaultPage()).isEqualTo("/updated");
        assertThat(updated.getDefaultPageAfterLogin()).isTrue();
        assertThat(updated.getDefaultPageAfterRefresh()).isFalse();
        assertThat(this.userRepository.findById(1L).orElseThrow().getSettings().getDefaultPage())
            .isEqualTo("/updated");
    }

    @Test
    @Order(4)
    void testUpdateAnotherUsersSettingsReturnsNotFound() throws Exception {
        UserSettingsDto request = new UserSettingsDto("/forbidden", true, true);
        this.addUrlParam("id", 2L);

        this.withExpectedStatus(HttpStatus.NOT_FOUND).runPut(
            UserController.PATH_UPDATE_USER_SETTINGS,
            "test",
            request,
            UserSettingsDto.class
        );

        assertThat(this.userRepository.findById(2L).orElseThrow().getSettings().getDefaultPage())
            .isEqualTo("/other");
    }

    @Test
    @Order(5)
    void testUpdateSettingsWithoutAuthenticationReturnsUnauthorized() throws Exception {
        UserSettingsDto request = new UserSettingsDto("/unauthenticated", false, false);
        this.addUrlParam("id", 1L);

        this.withExpectedStatus(HttpStatus.UNAUTHORIZED).runPut(
            UserController.PATH_UPDATE_USER_SETTINGS,
            null,
            request,
            UserSettingsDto.class
        );
    }

    @Test
    @Order(6)
    void testUpdateSettingsWithMissingRequiredFieldsReturnsUnprocessableContent() throws Exception {
        this.addUrlParam("id", 1L);

        this.withExpectedStatus(HttpStatus.UNPROCESSABLE_CONTENT).runPut(
            UserController.PATH_UPDATE_USER_SETTINGS,
            "test",
            new UserSettingsDto(),
            UserSettingsDto.class
        );
    }

    @Test
    @Order(7)
    void testUpdateUnknownUsersSettingsReturnsNotFound() throws Exception {
        this.addUrlParam("id", 999L);

        this.withExpectedStatus(HttpStatus.NOT_FOUND).runPut(
            UserController.PATH_UPDATE_USER_SETTINGS,
            "test",
            new UserSettingsDto("/unknown", false, false),
            UserSettingsDto.class
        );
    }
}