package cz.ecis.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cz.ecis.core.security.EcisApiKeyDetails;
import cz.ecis.db.ent.ApiKeyEnt;
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
            if (auth.getPrincipal() instanceof EcisApiKeyDetails eakd) {
                ApiKeyEnt apiKey = eakd.getEnt();
                ThreadContext.put("apiKeyId", String.valueOf(apiKey.getId()));
                EcisContext.getRequest().getEntry().setApiKey(apiKey);
            }
            ThreadContext.put("apiKeyName", auth.getName());
        }

        EcisContext.getRequest().setSource("APPEXT");
        EcisLocaleContext.init(request);

        LOGGER.info("Called API");

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        
        int status = response.getStatus();
        boolean success = status >= 200 && status < 400;

        ThreadContext.put("success", String.valueOf(success));
        ThreadContext.put("duration", String.valueOf(EcisContext.getRequestTime()));
        EcisContext.getRequest().getEntry().setSuccess(success);

        LOGGER.info("API call done");
        ThreadContext.clearAll();
    }
}