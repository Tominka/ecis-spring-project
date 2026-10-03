package cz.ecis.core.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import cz.ecis.db.ent.ApiKeyEnt;
import cz.ecis.db.repo.ApiKeyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component 
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final ApiKeyRepository apiKeyRepository;
    private final AccountStatusUserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String apiKey = (String) authentication.getCredentials();

        String apiKeyHash = this.hashKey(apiKey);

        System.out.println(apiKeyHash);

        ApiKeyEnt principal = this.apiKeyRepository.findByKeyHash(apiKeyHash)
        .orElseThrow(() -> new BadCredentialsException("Invalid API key"));

        EcisApiKeyDetails details = new EcisApiKeyDetails(principal);

        userDetailsChecker.check(details);

        return new UsernamePasswordAuthenticationToken(details, null, List.of());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String hashKey(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}