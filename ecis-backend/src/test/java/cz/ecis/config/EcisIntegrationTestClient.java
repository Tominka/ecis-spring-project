package cz.ecis.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import cz.ecis.EcisTestApplication;
import cz.ecis.api.SecController;
import cz.ecis.config.liquibase.LiquibaseTest;
import cz.ecis.config.liquibase.TestLiquibaseAfter;
import cz.ecis.config.liquibase.TestLiquibaseBefore;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;

@SpringBootTest(classes = {EcisTestApplication.class})
@LiquibaseTest
@TestLiquibaseBefore(changeLog = "db/changelog/common/before-each-test.xml", dataSourceId = "liquibaseDataSource")
@TestLiquibaseAfter(changeLog = "db/changelog/common/after-each-test.xml", dataSourceId = "liquibaseDataSource")
public abstract class EcisIntegrationTestClient extends EcisIntegrationTest  {

    private static final String DEFAULT_API_ROUTE = "/api/v1";

    protected static final Path TEMP_DIR;

    static {
        try {
            TEMP_DIR = Files.createTempDirectory("uploads-test-");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("ecis.storage.directory", TEMP_DIR::toString);
        PostgreSQLContainer postgres = TestContainerConfig.getPostgresContainer();
        registry.add("spring.datasource.jdbc-url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.jdbc-url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.username", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @Override
    String getApiDefaultApiRoute() {
        return EcisIntegrationTestClient.DEFAULT_API_ROUTE;
    }
    
    @Override
    List<EcisTestUserCredential> obtainUserCredentials() throws Exception {
        return List.of(
            this.obtainUserCredential("test", "test")
        );
    }

    @Override
    String getAuthorizationHeader() {
        return "Authorization";
    }

    EcisTestUserCredential obtainUserCredential(String username, String password) throws Exception {
        LoginRequestDto dto = new LoginRequestDto(username, password);
        LoginResponseDto response = this.runPost(SecController.PATH_LOGIN, null, dto, LoginResponseDto.class);

        return new EcisTestUserCredential(username, "Bearer " + response.getToken());
    }
    
}
