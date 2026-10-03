package cz.ecis.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import cz.ecis.EcisTestApplication;
import cz.ecis.api.SecController;
import cz.ecis.config.liquibase.LiquibaseTest;
import cz.ecis.config.liquibase.TestLiquibaseAfter;
import cz.ecis.config.liquibase.TestLiquibaseBefore;
import cz.ecis.model.dto.LoginRequestDto;
import cz.ecis.model.dto.LoginResponseDto;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = {EcisTestApplication.class})
@LiquibaseTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestLiquibaseBefore(changeLog = "db/changelog/common/before-each-test.xml", dataSourceId = "liquibaseDataSource")
@TestLiquibaseAfter(changeLog = "db/changelog/common/after-each-test.xml", dataSourceId = "liquibaseDataSource")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
    "test.uuid=${random.uuid}"
})
@AutoConfigureMockMvc
public abstract class EcisIntegrationTest {

    private static final String DEFAULT_API_ROUTE = "/api/v1";
    
    @Autowired
    protected MockMvc mockMvc;

    private Map<String, String> usersTokens = new HashMap<>();

    private Map<String, Object> urlParams = new HashMap<>();

    @Autowired
    protected ObjectMapper om;

    protected String testUserToken = null;

    @BeforeAll
    void beforeAllTests() throws Exception {
        this.obtainUserToken("test", "test");
    }

    @AfterEach
    void afterEachTest() {
        this.urlParams.clear();
    }

    private void obtainUserToken(String username, String password) throws Exception {
        LoginRequestDto dto = new LoginRequestDto(username, password);
        String result = mockMvc.perform(
            post("/api/v1" + SecController.PATH_LOGIN)
                .contentType(MediaType.APPLICATION_JSON)
                .header("User-Agent", "JUnit-Test")
                .content(om.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        testUserToken = om.readValue(result, new TypeReference<LoginResponseDto>() {}).getToken();

        this.usersTokens.put(username, testUserToken);
    }

    protected void addUrlParam(String key, Object value) {
        this.urlParams.putIfAbsent(key, value);
    }

    protected <T> T runGet(String path, String user, Class<T> resultClass) throws Exception {
        MvcResult result = runClientGet(path, user);

        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    protected <T> T runGet(String path, String user, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClientGet(path, user);

        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    protected <T> T runPost(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClientPost(path, body, user);

        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    protected <T> T runPost(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClientPost(path, body, user);

        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    protected <T> T runPut(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClientPut(path, body, user);

        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    protected <T> T runPut(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClientPut(path, body, user);

        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    private MvcResult runClientGet(String path, String user) throws Exception {
        path = this.resolveFinalPath(path);

        return mockMvc.perform(
            get(DEFAULT_API_ROUTE + path)
                .header("User-Agent", "JUnit-Test")
                .header(
                    "Authorization", "Bearer " + usersTokens.get(user)
                )
        )
        .andExpect(status().isOk())
        .andReturn();
    }

    private MvcResult runClientPost(String path, Object body, String user) throws Exception {
        path = this.resolveFinalPath(path);
        
        MockHttpServletRequestBuilder req = post(DEFAULT_API_ROUTE + path)
            .header("User-Agent", "JUnit-Test")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + usersTokens.get(user));

        if (body != null) {
            req.content(
                this.om.writeValueAsString(body)
            );
        }

        return mockMvc.perform(req)
            .andExpect(status().isOk())
            .andReturn();
    }

    private MvcResult runClientPut(String path, Object body, String user) throws Exception {
        path = this.resolveFinalPath(path);
        
        MockHttpServletRequestBuilder req = put(DEFAULT_API_ROUTE + path)
            .header("User-Agent", "JUnit-Test")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + usersTokens.get(user));

        if (body != null) {
            req.content(
                this.om.writeValueAsString(body)
            );
        }

        return mockMvc.perform(req)
            .andExpect(status().isOk())
            .andReturn();
    }

    private String resolveFinalPath(String path) {
        for (Map.Entry<String, Object> param : this.urlParams.entrySet()) {
            path = path.replace(
                "{" + param.getKey() + "}",
                String.valueOf(param.getValue())
            );
        }

        return path;
    }
    
}
