package cz.ecis.core.security;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	private final Serializable principal;
    private Serializable credentials;

    public JwtAuthenticationToken(String username, String token) {
        super((Collection<GrantedAuthority>) null);
        this.principal = username;
        this.credentials = token;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(UserDetails principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
