package cz.ecis.core.security.annotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import cz.ecis.core.EcisContext;
import cz.ecis.core.exception.CampResolveException;
import cz.ecis.core.security.CampSecurityContext;
import cz.ecis.db.ent.UserEnt;
import cz.ecis.utils.SecurityUtils;

@Aspect
@Component
public class CampSecuredAspect {

    public static final Logger LOGGER = LogManager.getLogger();

    @Before("@annotation(campSecured)")
    public void check(CampSecured campSecured) {
        EcisContext.getRequest().getEntry().setAuthorized(false);
        try {
            Long campId = CampSecurityContext.getCampId();
            if (campId == null || campId <= 0) {
                UserEnt user = SecurityUtils.getUserEnt();
                LOGGER.warn("No camp ID provided in request, but user {} is authenticated. Access to camp-specific resources is not allowed.", user != null ? user.getUsername() : "anonymous");
                throw new AccessDeniedException("Camp ID is required for accessing camp-specific resources");
            }
        }
        catch (CampResolveException e) {
            throw e;
        }
        catch (Exception _) {
            throw new CampResolveException("Required Camp id is not set");
        }

        if (campSecured.roles().length == 0 || SecurityUtils.hasAnyRole(campSecured.roles())) {
            EcisContext.getRequest().getEntry().setAuthorized(true);
            return; // User has at least one of the required roles, allow access
        }
        throw new AccessDeniedException("User does not have the required roles");
    }
}
