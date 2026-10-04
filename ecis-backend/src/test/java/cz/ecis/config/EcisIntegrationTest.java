package cz.ecis.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
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
import java.util.Map.Entry;

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

    private Map<String, String> additionalHeaders = new HashMap<>();

    private HttpStatus expectedStatus = HttpStatus.OK;

    private boolean expectedEmptyContent = false;

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

    public EcisIntegrationTest withHeader(String header, String value) {
        this.additionalHeaders.put(header, value);
        return this;
    }

    public EcisIntegrationTest withUserToken(String key, String value) {
        this.usersTokens.put(key, value);
        return this;
    }

    public void clearHeaders() {
        this.additionalHeaders = new HashMap<>();
    }

    public EcisIntegrationTest withExpectedStatus(HttpStatus status) {
        this.expectedStatus = status;
        return this;
    }

    public EcisIntegrationTest withExpectedEmptyContent() {
        this.expectedEmptyContent = true;
        this.withExpectedStatus(HttpStatus.NO_CONTENT);
        return this;
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

    public void addUrlParam(String key, Object value) {
        this.urlParams.putIfAbsent(key, value);
    }

    public <T> T runGet(String path, String user, Class<T> resultClass) throws Exception {
        MvcResult result = runClientGet(path, user);

        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    public <T> T runGet(String path, String user, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClientGet(path, user);

        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    public <T> T runPost(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClientPost(path, body, user);
        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    public <T> T runPost(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClientPost(path, body, user);

        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    public <T> T runPut(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClientPut(path, body, user);

        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    public <T> T runPut(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClientPut(path, body, user);

        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    public <T> T runPatch(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClientPatch(path, body, user);

        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    public <T> T runPatch(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClientPatch(path, body, user);

        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }
        return om.readValue(
            result.getResponse().getContentAsString(),
            resultClass
        );
    }

    private MvcResult runClientGet(String path, String user) throws Exception {
        path = this.resolveFinalPath(path);

        MockHttpServletRequestBuilder req = get(DEFAULT_API_ROUTE + path)
            .header("User-Agent", "JUnit-Test")
            .contentType(MediaType.APPLICATION_JSON);

        if (user != null && usersTokens.containsKey(user)) {
            req.header("Authorization", "Bearer " + usersTokens.get(user));
        }

        for (Entry<String, String> header : this.additionalHeaders.entrySet()) {
            req.header(header.getKey(), header.getValue());
        }

        ResultActions result = mockMvc.perform(req)
            .andExpect(status().is(this.expectedStatus.value()));
        
        if (this.expectedEmptyContent) {
            result = result.andExpect(status().isNoContent());
        }
        
        this.expectedStatus = HttpStatus.OK;
        this.additionalHeaders.clear();

        return result.andReturn();
    }

    private MvcResult runClientPost(String path, Object body, String user) throws Exception {
        path = this.resolveFinalPath(path);
        
        MockHttpServletRequestBuilder req = post(DEFAULT_API_ROUTE + path)
            .header("User-Agent", "JUnit-Test")
            .contentType(MediaType.APPLICATION_JSON);

        if (user != null && usersTokens.containsKey(user)) {
            req.header("Authorization", "Bearer " + usersTokens.get(user));
        }

        for (Entry<String, String> header : this.additionalHeaders.entrySet()) {
            req.header(header.getKey(), header.getValue());
        }

        if (body != null) {
            req.content(
                this.om.writeValueAsString(body)
            );
        }

        ResultActions result = mockMvc.perform(req)
            .andExpect(status().is(this.expectedStatus.value()));
        
        if (this.expectedEmptyContent) {
            result = result.andExpect(status().isNoContent());
        }
        
        this.expectedStatus = HttpStatus.OK;
        this.additionalHeaders.clear();

        return result.andReturn();
    }

    private MvcResult runClientPut(String path, Object body, String user) throws Exception {
        path = this.resolveFinalPath(path);
        
        MockHttpServletRequestBuilder req = put(DEFAULT_API_ROUTE + path)
            .header("User-Agent", "JUnit-Test")
            .contentType(MediaType.APPLICATION_JSON);

        if (user != null && usersTokens.containsKey(user)) {
            req.header("Authorization", "Bearer " + usersTokens.get(user));
        }

        if (body != null) {
            req.content(
                this.om.writeValueAsString(body)
            );
        }

        ResultActions result = mockMvc.perform(req)
            .andExpect(status().is(this.expectedStatus.value()));
        
        if (this.expectedEmptyContent) {
            result = result.andExpect(status().isNoContent());
        }
        
        this.expectedStatus = HttpStatus.OK;
        this.additionalHeaders.clear();

        return result.andReturn();
    }

    private MvcResult runClientPatch(String path, Object body, String user) throws Exception {
        path = this.resolveFinalPath(path);
        
        MockHttpServletRequestBuilder req = patch(DEFAULT_API_ROUTE + path)
            .header("User-Agent", "JUnit-Test")
            .contentType(MediaType.APPLICATION_JSON);

        if (user != null && usersTokens.containsKey(user)) {
            req.header("Authorization", "Bearer " + usersTokens.get(user));
        }
    
        for (Entry<String, String> header : this.additionalHeaders.entrySet()) {
            req.header(header.getKey(), header.getValue());
        }

        if (body != null) {
            req.content(
                this.om.writeValueAsString(body)
            );
        }

        ResultActions result = mockMvc.perform(req)
            .andExpect(status().is(this.expectedStatus.value()));
        
        if (this.expectedEmptyContent) {
            result = result.andExpect(status().isNoContent());
        }
        
        this.expectedStatus = HttpStatus.OK;
        this.additionalHeaders.clear();

        return result.andReturn();
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
