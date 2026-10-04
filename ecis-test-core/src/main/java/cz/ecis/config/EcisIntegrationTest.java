package cz.ecis.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
    "test.uuid=${random.uuid}"
})
@AutoConfigureMockMvc
public abstract class EcisIntegrationTest {
    
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper om;

    private Map<String, String> usersTokens = new HashMap<>();

    private Map<String, Object> urlParams = new HashMap<>();

    private Map<String, String> additionalHeaders = new HashMap<>();

    private HttpStatus expectedStatus = HttpStatus.OK;

    private boolean expectedEmptyContent = false;

    protected String testUserToken = null;

    abstract String getApiDefaultApiRoute();
    
    abstract List<EcistTestUserCredential> obtainUserCredetnials() throws Exception;

    @BeforeAll
    void beforeAllTests() throws Exception {
        this.registerUserCredentials();
    }

    @AfterEach
    void afterEachTest() {
        this.urlParams.clear();
    }

    private void registerUserCredentials() throws Exception {
        List<EcistTestUserCredential> credentials = this.obtainUserCredetnials();
        if (credentials != null) {
            for (EcistTestUserCredential credential : credentials) {
                 this.usersTokens.put(credential.name(), credential.token());
            }
        }
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

    public void addUrlParam(String key, Object value) {
        this.urlParams.putIfAbsent(key, value);
    }

    public <T> T runGet(String path, String user, Class<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.GET, path, null, user);
        return this.readResult(result, om.constructType(resultClass));
    }

    public <T> T runGet(String path, String user, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.GET, path, null, user);
        return this.readResult(result, om.constructType(resultClass.getType()));
    }

    public <T> T runPost(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.POST, path, body, user);
        return this.readResult(result, om.constructType(resultClass));
    }

    public <T> T runPost(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.POST, path, body, user);
        return this.readResult(result, om.constructType(resultClass.getType()));
    }

    public <T> T runPut(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.PUT, path, body, user);
        return this.readResult(result, om.constructType(resultClass));
    }

    public <T> T runPut(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.PUT, path, body, user);
        return this.readResult(result, om.constructType(resultClass.getType()));
    }

    public <T> T runPatch(String path, String user, Object body, Class<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.PATCH, path, body, user);
        return this.readResult(result, om.constructType(resultClass));
    }

    public <T> T runPatch(String path, String user, Object body, TypeReference<T> resultClass) throws Exception {
        MvcResult result = runClient(EcisRequestMethod.PATCH, path, body, user);
        return this.readResult(result, om.constructType(resultClass.getType()));
    }

    private <T> T readResult(MvcResult result, JavaType resultType) throws JacksonException, UnsupportedEncodingException {
        if (this.expectedEmptyContent) {
            this.expectedEmptyContent = false;
            return null;
        }

        return om.readValue(
            result.getResponse().getContentAsString(),
            resultType
        );
    }

    private MvcResult runClient(
        EcisRequestMethod method, String path, Object body, String user
    ) throws Exception {
        MockHttpServletRequestBuilder req = this.resolveRequest(path, method)
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

    private MockHttpServletRequestBuilder resolveRequest(String path, EcisRequestMethod method) {
        String finalPath = this.getApiDefaultApiRoute() + this.resolveFinalPath(path);
        switch (method) {
            case GET:
                return get(finalPath);
            case POST:
                return post(finalPath);
            case PUT:
                return put(finalPath);
            case PATCH:
                return patch(finalPath);
            case DELETE:
                return delete(finalPath);
            default:
                return get(finalPath);
        }
    }

    public enum EcisRequestMethod {
        GET, POST, PUT, PATCH, DELETE;
    }

    public static record EcistTestUserCredential(String name, String token) {}
    
}
