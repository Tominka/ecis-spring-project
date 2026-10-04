package cz.ecis.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/ext/v1")
@RequiredArgsConstructor
public class TestController implements TestApiInterface {

    public static final String TEST_OK_RESPONSE = "OK";

    @Override
    public ResponseEntity<String> getTestResponse() {
        return ResponseEntity.ok(TestController.TEST_OK_RESPONSE);
    }
    
}
