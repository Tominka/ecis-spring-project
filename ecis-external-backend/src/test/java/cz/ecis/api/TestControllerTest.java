package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import cz.ecis.config.EcisIntegrationTestClient;

public class TestControllerTest extends EcisIntegrationTestClient {

    @Test
    @Order(1)
    void testTestResponseOk() throws Exception {

        String response = this.runGet(TestController.PATH_GET_TEST_RESPONSE, "test", String.class);

        assertThat(response).isEqualTo(TestController.TEST_OK_RESPONSE);
    }

    @Test
    @Order(2)
    void testTestResponseDisabledApikey() throws Exception {

        String response = this.withExpectedStatus(HttpStatus.UNAUTHORIZED)
            .runGet(TestController.PATH_GET_TEST_RESPONSE, "test_disabled", String.class);

        assertThat(response).isNull();
    }
}