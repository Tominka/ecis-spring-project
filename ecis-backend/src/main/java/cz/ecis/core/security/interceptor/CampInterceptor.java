package cz.ecis.core.security.interceptor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.HandlerInterceptor;

import cz.ecis.core.EcisContext;
import cz.ecis.core.exception.CampResolveException;
import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.security.CampSecurityContext;
import cz.ecis.core.security.EcisRoleEnum;
import cz.ecis.core.security.EcisUserDetails;
import cz.ecis.db.ent.CampEnt;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.db.repo.CampRepository;
import cz.ecis.db.repo.CampRoleRepository;
import cz.ecis.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Component
@Validated
@RequiredArgsConstructor
public class CampInterceptor implements HandlerInterceptor {

    public static final Logger LOGGER = LogManager.getLogger(CampInterceptor.class);

    private final CampRoleRepository campRoleRepository;
    private final CampRepository campRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        EcisContext.getRequest().getEntry().setAuthorized(false);

        Long campId;

        try {
            campId = this.extractCampId(request);
            LOGGER.debug("Resolved camp ID {}", campId);
        } catch (CampResolveException ex) {
            LOGGER.error("Error resolving camp ID: {}", ex.getMessage());
            throw new CampResolveException("Invalid camp ID format");
        }

        if (campId != null) {
            CampEnt camp = this.campRepository.findById(campId).orElseThrow(() -> {
                LOGGER.error("Camp with ID {} does not exist", campId);
                return new EntityNotExistsException("Camp with given ID does not exist", campId);
            });

            if (auth != null && auth.isAuthenticated()) {
                CampSecurityContext.setCamp(camp);
                this.resolveUserCampRoles(auth, campId);
            }
        }

        EcisContext.getRequest().getEntry().setAuthorized(true);
        return true;
    }

    private void resolveUserCampRoles(@NotNull Authentication auth, Long campId) {
        EcisUserDetails user = (EcisUserDetails) auth.getPrincipal();
        UserEnt userEnt = user == null ? null : user.getEnt();
        if (userEnt == null) {
            LOGGER.error("User {} does not have EcisUserDetails, unable to authenticate to camp {}", auth.getName(), campId);
            throw new AccessDeniedException("User does not have access to camp beacuse is unauthicated");
        } else {
            if (!userEnt.isSystemAdmin()) { // System user has all roles from EcisDetails. This is applied for basic user
                List<String> roles = this.campRoleRepository.findAllByRoleGlobalFalseAndRoleEnabledTrueAndUserIdAndCampId(userEnt.getId(), campId).stream()
                    .map(r -> r.getRole().getCode())
                .toList();

                List<GrantedAuthority> merged = Stream.concat(
                    auth.getAuthorities().stream(),
                    roles.stream().map(SimpleGrantedAuthority::new)
                ).toList();

                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        auth.getPrincipal(),
                        auth.getCredentials(),
                        merged
                    )
                );
            }
            if (!SecurityUtils.hasAnyRole(EcisRoleEnum.USER)) {
                LOGGER.warn("User {} does not have access to camp {}", auth.getName(), campId);
                throw new AccessDeniedException("User does not have access to camp");
            }
        }
    }

    private Long extractCampId(HttpServletRequest request) {
        String campId = request.getHeader("X-Camp-Id");

        LOGGER.debug("Resolved X-Camp-Id header {}", campId);

        if (campId == null || campId.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(campId);
        } catch (NumberFormatException _) {
            LOGGER.warn("Invalid X-Camp-Id header value: {}", campId);
            return null;
        }
    }
}