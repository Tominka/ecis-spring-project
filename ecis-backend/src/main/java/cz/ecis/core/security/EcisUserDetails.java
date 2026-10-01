package cz.ecis.core.security;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import cz.ecis.db.ent.CampEnt;
import cz.ecis.db.ent.UserEnt;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EcisUserDetails implements UserDetails {

    @Getter
    private final UserEnt ent;

    private final List<String> globalRoles;

    @Getter
    private final List<CampEnt> userCamps;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.globalRoles.stream()
            .map(SimpleGrantedAuthority::new)
        .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.ent == null ? "anonymous" : this.ent.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return this.ent.isEnabled();
    }
}
