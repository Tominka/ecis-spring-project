package cz.ecis.config.db;

public class DbConstants {

    private DbConstants() {
        /* This utility class should not be instantiated */
    }

    public static final String ECIS_PERSISTANCE_UNIT = "ecisPersistanceUnit";
    public static final String ECIS_ENTITY_MANAGER = "ecisEntityManager";
    public static final String ECIS_TRANSACTION_MANAGER = "ecisTransactionManager";

    public static final String ECIS_ENT_BASE_PACKAGE =  "cz.ecis.db.ent";
    public static final String ECIS_REPO_BASE_PACKAGE =  "cz.ecis.db.repo";
}
