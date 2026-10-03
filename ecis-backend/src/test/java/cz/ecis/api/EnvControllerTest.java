package cz.ecis.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestConstructor;

import cz.ecis.config.EcisIntegrationTest;
import cz.ecis.core.environment.EnvTypeEnum;
import cz.ecis.model.dto.EnvDto;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;

@RequiredArgsConstructor 
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class EnvControllerTest extends EcisIntegrationTest {

    private final BuildProperties buildProperties;

    @Test
    @Order(1)
    void testGetEnv() throws Exception {

        EnvDto env = this.runGet(EnvController.PATH_GET_APP_ENV, "test", new TypeReference<EnvDto>() {});

        assertThat(env.getType()).isEqualTo(EnvTypeEnum.TEST);
        assertThat(env.getDisplayName()).isEqualTo("TestApp");
        assertThat(env.getVersion()).isEqualTo(buildProperties.getVersion());
    }
}