package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import cz.ecis.config.EcisIntegrationTestClient;
import cz.ecis.core.model.LovDto;
import cz.ecis.model.dto.CampDto;
import tools.jackson.core.type.TypeReference;


class UserControllerTest extends EcisIntegrationTestClient {

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
}