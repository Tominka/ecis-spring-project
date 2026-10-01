package cz.ecis.utils;

import cz.ecis.core.exception.EntityIdViolation;
import cz.ecis.core.exception.EntityVersionViolation;
import cz.ecis.core.exception.IllegalRecordStateException;

public class CrudUtils {

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
}
