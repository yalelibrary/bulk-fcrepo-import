package edu.yale.library.ladybird.kernel.derby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * A facade to derby.
 */
public final class EmbeddedDatabaseUtil {
    private static Logger logger = LoggerFactory.getLogger(EmbeddedDatabaseUtil.class);
    private EmbeddedDatabaseUtil() {
    }

    public static void start() {
        DerbyManager.getINSTANCE().start();
    }

    public static void stop() throws SQLException{
        try {
            new DerbySchemaUtil().killSchema();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        DerbyManager.getINSTANCE().stop();
    }

    public static boolean isDatabaseRunning() {
        return DerbyManager.getINSTANCE().isRUNNING();
    }

    public static void init() {
        new DerbySchemaUtil().init(); //TODO
    }

}
