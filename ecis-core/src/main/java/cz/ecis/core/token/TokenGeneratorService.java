package cz.ecis.core.token;

import java.security.SecureRandom;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Service 
public class TokenGeneratorService {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateRandom() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public TokenGeneratorResult generateRandomSha256() {
        String raw = this.generateRandom();
        return new TokenGeneratorResult(raw, DigestUtils.sha256Hex(raw));
    }

    public record TokenGeneratorResult(String raw, String hash) {};
}
