package cz.ecis.config;

import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;

import cz.ecis.EcisTestApplication;
import cz.ecis.config.liquibase.LiquibaseTest;
import cz.ecis.config.liquibase.TestLiquibaseAfter;
import cz.ecis.config.liquibase.TestLiquibaseBefore;

@SpringBootTest(classes = {EcisTestApplication.class})
@LiquibaseTest
@TestLiquibaseBefore(changeLog = "db/changelog/common/before-each-test.xml", dataSourceId = "liquibaseDataSource")
@TestLiquibaseAfter(changeLog = "db/changelog/common/after-each-test.xml", dataSourceId = "liquibaseDataSource")
public abstract class EcisIntegrationTestClient extends EcisIntegrationTest  {

    private static final String DEFAULT_API_ROUTE = "/api/ext/v1";

    @Override 
    String getApiDefaultApiRoute() {
        return EcisIntegrationTestClient.DEFAULT_API_ROUTE;
    }
    
    
    @Override
    List<EcisTestUserCredential> obtainUserCredentials() throws Exception {
        return List.of(
            this.obtainCredential("test", "test"),
            this.obtainCredential("test_disabled", "test_disabled")
        );
    }

    @Override
    String getAuthorizationHeader() {
        return "X-API-Key";
    }

    EcisTestUserCredential obtainCredential(String name, String key) throws Exception {
        return new EcisTestUserCredential(name, key); 
    }
    
}
