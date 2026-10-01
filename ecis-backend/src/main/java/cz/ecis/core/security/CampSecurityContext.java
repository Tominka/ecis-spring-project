package cz.ecis.core.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cz.ecis.db.ent.CampEnt;

public class CampSecurityContext {

    public static final Logger LOGGER = LogManager.getLogger();

    private CampSecurityContext() {
        /* This utility class should not be instantiated */
    }

    private static final ThreadLocal<CampEnt> CAMP = new ThreadLocal<>();

    public static void setCamp(CampEnt camp) {
        CAMP.set(camp);
    }

    public static CampEnt getCamp() {
        CampEnt camp = CAMP.get();
        if (camp == null) {
            LOGGER.warn("Camp is not set in the security context (is null). This may indicate a misconfiguration or an attempt to access camp-specific resources without proper context.");
        }
        return camp;
    }

    public static Long getCampId() {
        CampEnt camp = getCamp();
        if (camp == null) {
            return null;
        }

        return camp.getId();
    }

    public static void clear() {
        CAMP.remove();
    }
}