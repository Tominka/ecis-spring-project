package cz.ecis.core.security;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import cz.ecis.db.ent.ApiKeyEnt;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EcisApiKeyDetails implements UserDetails {

    @Getter
    private final ApiKeyEnt ent;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.ent == null ? "anonymous" : this.ent.getName();
    }

    @Override
    public boolean isEnabled() {
        return this.ent.isEnabled();
    }
}
