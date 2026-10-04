package cz.ecis.config;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;

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

    @Override 
    String getApiDefaultApiRoute() {
        return EcisIntegrationTestClient.DEFAULT_API_ROUTE;
    }
    
    @Override
    List<EcistTestUserCredential> obtainUserCredetnials() throws Exception {
        return List.of(
            this.obtainUserCredetnial("test", "test")
        );
    }

    EcistTestUserCredential obtainUserCredetnial(String username, String password) throws Exception {
        LoginRequestDto dto = new LoginRequestDto(username, password);
        LoginResponseDto response = this.runPost(SecController.PATH_LOGIN, null, dto, LoginResponseDto.class);

        return new EcistTestUserCredential(username, response.getToken());
    }
    
}
