package cz.ecis.core;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import cz.ecis.db.ent.LogEntryEnt;
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

    private static LogEntryEnt newLogEntry(HttpServletRequest request) {
        LogEntryEnt entry = new LogEntryEnt();
        entry.setIpAddress(StringUtils.abbreviate(request.getRemoteAddr(), "", 16));
        entry.setPoint(request.getRequestURI());
        entry.setUserAgent(request.getHeader("User-Agent"));
        entry.setAuthenticated(false);
        entry.setAuthorized(false);
        entry.setCreatedAt(
            OffsetDateTime.now(ZoneId.systemDefault())
        );
        entry.setRefresh(false);
        entry.setPasswordExpired(false);
        entry.setSessionId(null);

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
        private LogEntryEnt entry;

        //TODO: udělat z toho enum
        private String source;

        private UUID requestId = UUID.randomUUID();
        private Long startMilis = System.currentTimeMillis();
    }
    
}
