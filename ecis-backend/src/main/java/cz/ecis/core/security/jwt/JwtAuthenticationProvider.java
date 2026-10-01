package cz.ecis.core.security.jwt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import cz.ecis.core.security.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final AccountStatusUserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            return null;
        }

        String username = (String) jwtAuthenticationToken.getPrincipal();
        UserDetails user = userDetailsService.loadUserByUsername(username);
        userDetailsChecker.check(user);

        JwtAuthenticationToken authenticatedToken = new JwtAuthenticationToken(user, user.getAuthorities());
        authenticatedToken.setDetails(jwtAuthenticationToken.getDetails());
        return authenticatedToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
