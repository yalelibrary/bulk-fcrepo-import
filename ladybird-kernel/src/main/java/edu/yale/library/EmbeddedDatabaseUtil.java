package edu.yale.library;

import java.sql.SQLException;

/**
 * A facade to derby. Subject to change.
 */
public final class EmbeddedDatabaseUtil {
    private EmbeddedDatabaseUtil() {
    }

    protected static void start() {
        DerbyManager.getINSTANCE().start();
    }

    protected static void stop() throws SQLException {
        DerbyManager.getINSTANCE().stop();
    }

    protected static boolean isDatabaseRunning() {
        return DerbyManager.getINSTANCE().isRUNNING();
    }

    protected static void init() {
        new DerbySchemaUtil().init(); //TODO
    }

}
