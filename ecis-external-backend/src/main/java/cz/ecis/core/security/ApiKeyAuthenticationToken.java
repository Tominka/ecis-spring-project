package cz.ecis.core.security;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyAuthenticationToken(String apiKey) {
        super(List.of());
        this.apiKey = apiKey;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}