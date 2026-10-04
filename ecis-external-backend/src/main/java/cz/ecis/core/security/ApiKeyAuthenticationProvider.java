package cz.ecis.core.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import cz.ecis.core.service.ApiKeyHashService;
import cz.ecis.db.ent.ApiKeyEnt;
import cz.ecis.db.repo.ApiKeyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component 
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final ApiKeyHashService apiKeyHashService;
    private final ApiKeyRepository apiKeyRepository;
    private final AccountStatusUserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String apiKey = (String) authentication.getCredentials();

        String apiKeyHash = this.apiKeyHashService.hashKey(apiKey);

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

}