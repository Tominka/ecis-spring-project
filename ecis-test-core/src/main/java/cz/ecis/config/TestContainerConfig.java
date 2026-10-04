package cz.ecis.config;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @SuppressWarnings({"resource", "java:S2095"})
    PostgreSQLContainer postgreContainer() {
        PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:18.4")
        )
            .withDatabaseName("ecis")
            .withUsername("ecis")
            .withPassword("ecis")
            .withInitScript("db/init.sql");

        postgres.setPortBindings(List.of("15432:5432"));

        postgres.waitingFor(
            Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 1)
        );

        return postgres;
    }

    @Bean
    @Primary
    @DependsOn(value = "postgreContainer")
    String postgreContainerDependency() {
        return StringUtils.EMPTY;
    }
}