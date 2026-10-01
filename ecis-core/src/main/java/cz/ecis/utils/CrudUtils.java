package cz.ecis.utils;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import cz.ecis.core.ent.ICampEntity;
import cz.ecis.core.ent.lookup.EcisLookupEntity;
import cz.ecis.core.exception.EntityIdViolation;
import cz.ecis.core.exception.EntityNotExistsException;
import cz.ecis.core.exception.EntityVersionViolation;
import cz.ecis.core.exception.IllegalRecordStateException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class CrudUtils {

    public static final Logger LOGGER = LogManager.getLogger();

    private CrudUtils() {
        /* This utility class should not be instantiated */
    }

    public static void checkIdViolation(Object id1, Object id2) {
        if (id1 == null) {
            throw new IllegalRecordStateException("Source record id is not presented");
        }
        if (id2 == null) {
            throw new IllegalRecordStateException("Recieved record id is not presented");
        }
        if (!id1.equals(id2)) {
            throw new EntityIdViolation("Entity id violation", id1, id2);
        }
    }

    public static void checkEntityVersion(Object version1, Object version2) {
        if (version1 == null) {
            throw new IllegalRecordStateException("Source record version is not presented");
        }
        if (version2 == null) {
            throw new IllegalRecordStateException("Recieved record version is not presented");
        }
        if (!version1.equals(version2)) {
            throw new EntityVersionViolation("Entity version violation", version1, version2);
        }
    }

     public static void checkLookupValidity(EcisLookupEntity<? extends Serializable> entity) {
        if (entity == null) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneId.systemDefault());

        if (entity.getValidFrom().isAfter(now) || (entity.getValidTo() != null && !entity.getValidTo().isAfter(now))) {
            throw new IllegalRecordStateException("Record is not valid");
        }
        
    }

    public static void checkEntityCamp(ICampEntity ent, Long campId) {
        if (ent == null) {
            return;
        }

        if (ent.getCamp() == null || ent.getCamp().getId() == null) {
            LOGGER.warn(
                String.format("Record %s has no camp assigned or camp id is not defined (is null). Permission denied for obtain", ent.getClass())
            );
            throw new EntityNotExistsException("Record not found", null);
        }

        if (!ent.getCamp().getId().equals(campId)) {
            LOGGER.warn(
                String.format("Record %s has incorrect camp check. {record_camp=%s;requested_camp=%s}. Permission denied for obtain", ent.getClass(), ent.getCamp().getId(), campId)
            );
            throw new EntityNotExistsException("Record not found", null);
        }
    }
}
