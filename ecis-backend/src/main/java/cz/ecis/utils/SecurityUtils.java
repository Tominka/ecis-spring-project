package cz.ecis.utils;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.security.EcisRoleEnum;
import cz.ecis.core.security.EcisUserDetails;
import cz.ecis.db.ent.CampEnt;
import cz.ecis.db.ent.UserEnt;

public class SecurityUtils {

    private static final Logger LOG = LogManager.getLogger(SecurityUtils.class);

    private SecurityUtils() {
        /* This utility class should not be instantiated */
    }

    public static UserEnt getUserEnt() {
        EcisUserDetails eud = getUserDetails();
        if (eud == null) {
            return null;
        }

        return eud.getEnt();
    }

    public static List<CampEnt> getUserCamps() {
        EcisUserDetails eud = getUserDetails();
        if (eud == null) {
            return List.of();
        }

        return eud.getUserCamps();
    }

    public static CampEnt getUserCampById(Long id) {
        return getUserCamps().stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new EntityNotExistsException("Camp with given id not found", id));
    }

    public static List<Long> getUserCampsId() {
        return getUserCamps().stream().map(e -> e.getId()).toList();
    }

    public static Long getUserId() {
        UserEnt ent = getUserEnt();

        if (ent == null) {
            return null;
        }

        return ent.getId();
    }

    private static EcisUserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof EcisUserDetails eud) {
            return eud;
        }

        return null;
    }

    public static List<String> getUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return List.of();
        }

        return auth.getAuthorities().stream()
            .map(a -> a.getAuthority())
        .toList();
    }

    public static boolean hasRole(EcisRoleEnum role) {
        return SecurityUtils.hasRole(role.getCode());
    }

    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return false;
        }

        for (var authority : auth.getAuthorities()) {
            if (role.equals(authority.getAuthority())) {
                LOG.debug("User {} has global role {}", auth.getName(), role);
                return true;
            }
        }

        LOG.warn("User {} does not have global role {}", auth.getName(), role);

        return false;
    }

    public static boolean hasAnyRole(EcisRoleEnum... roles) {
        for (EcisRoleEnum role : roles) {
            if (SecurityUtils.hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public record CampAccess(boolean exists, boolean hasAccess){}

}
