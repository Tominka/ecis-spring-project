package cz.ecis.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cz.ecis.core.security.EcisUserDetails;
import cz.ecis.localization.EcisLocaleContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiInterceptor implements HandlerInterceptor {

    public static final Logger LOGGER = LogManager.getLogger(ApiInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            if (auth.getPrincipal() instanceof EcisUserDetails eud) {
                ThreadContext.put("userId", String.valueOf(eud.getEnt().getId()));
                EcisContext.getRequest().getEntry().setUserId(eud.getEnt().getId());
            }
            ThreadContext.put("name", auth.getName());
            EcisContext.getRequest().getEntry().setUsername(auth.getName());
        }

        EcisContext.getRequest().setSource("APP");
        EcisLocaleContext.init(request);

        LOGGER.info("Called API");

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        
        ThreadContext.put("duration", String.valueOf(EcisContext.getRequestTime()));
        LOGGER.info("API call done");
        ThreadContext.clearAll();
        EcisLocaleContext.clear();
    }
}