package cz.ecis.core;

import java.io.IOException;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import cz.ecis.db.repo.ApiKeyEntryRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final ApiKeyEntryRepository apiKeyEntryRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        EcisContext.init(request);
        ThreadContext.put("requestId", EcisContext.getRequest().getRequestId().toString());

        try {
            chain.doFilter(request, response);
        } finally {
            this.apiKeyEntryRepository.save(EcisContext.getRequest().getEntry());
            EcisContext.clear();
        }
    }
}