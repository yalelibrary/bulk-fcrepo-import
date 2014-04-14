package edu.yale.library.ladybird.persistence.derby;

import java.sql.SQLException;

/**
 * A facade to derby. Subject to change.
 */
public final class EmbeddedDatabaseUtil {
    private EmbeddedDatabaseUtil() {
    }

    public static void start() {
        DerbyManager.getINSTANCE().start();
    }

    public static void stop() throws SQLException {
        DerbyManager.getINSTANCE().stop();
    }

    public static boolean isDatabaseRunning() {
        return DerbyManager.getINSTANCE().isRUNNING();
    }

    public static void init() {
        new DerbySchemaUtil().init(); //TODO
    }

}
