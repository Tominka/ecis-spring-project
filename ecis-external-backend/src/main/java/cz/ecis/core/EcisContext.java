package cz.ecis.core;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cz.ecis.db.ent.ApiKeyEntryEnt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

public class EcisContext {

    private EcisContext() {
        /* This utility class should not be instantiated */
    }

    private static final ThreadLocal<EcisRequest> REQUEST = new ThreadLocal<>();

    protected static void init(HttpServletRequest request) {
        EcisRequest ent = new EcisRequest();
        ent.setEntry(newLogEntry(request));

        REQUEST.set(ent);
    }

    public static EcisRequest getRequest() {
        return REQUEST.get();
    }

    private static ApiKeyEntryEnt newLogEntry(HttpServletRequest request) {
        ApiKeyEntryEnt entry = new ApiKeyEntryEnt();
        entry.setIpAddress(StringUtils.abbreviate(request.getRemoteAddr(), "", 16));
        entry.setEndpoint(request.getRequestURI());
        entry.setUserAgent(request.getHeader("User-Agent"));
        entry.setPayloadLength(request.getContentLength());
        entry.setCreatedAt(
            OffsetDateTime.now(ZoneId.systemDefault())
        );

        return entry;
    }

    protected static void clear() {
        REQUEST.remove();
    }

    public static Long getRequestTime() {
        return System.currentTimeMillis() - REQUEST.get().getStartMilis();
    }

    @Setter
    @Getter
    public static class EcisRequest {
        private ApiKeyEntryEnt entry;

        private String source;

        private UUID requestId = UUID.randomUUID();
        private Long startMilis = System.currentTimeMillis();
    }
    
}
