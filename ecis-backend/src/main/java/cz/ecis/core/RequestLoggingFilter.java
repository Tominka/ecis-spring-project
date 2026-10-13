package cz.ecis.core;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import cz.ecis.db.repo.LogEntryRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LogManager.getLogger(RequestLoggingFilter.class);

    private final LogEntryRepository logEntryRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        EcisContext.init(request);
        ThreadContext.put("requestId", EcisContext.getRequest().getRequestId().toString());

        try {
            chain.doFilter(request, wrappedResponse);
        } finally {
            try {
                EcisContext.EcisRequest requestContext = EcisContext.getRequest();
                this.logEntryRepository.save(requestContext.getEntry());
            } catch (RuntimeException ex) {
                LOGGER.error("Failed to save request log", ex);
            } finally {
                ThreadContext.remove("requestId");
                EcisContext.clear();
                wrappedResponse.copyBodyToResponse();
            }
        }
    }
}