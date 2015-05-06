package edu.yale.library.ladybird.kernel.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public final class EmbeddedDBServicesManager {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedDBServicesManager.class);

    public void initDB() {
        startDB();
        EmbeddedDBUtil.init();
    }

    public void startDB() {
        if (EmbeddedDBUtil.isDatabaseRunning()) {
            throw new EmbeddedDBException("Driver already RUNNING.");
        }
        logger.debug("Trying to start and init embedded DB");
        EmbeddedDBUtil.start();
    }

    public void stopDB() throws SQLException {
        if (!EmbeddedDBUtil.isDatabaseRunning()) {
            throw new EmbeddedDBException("Driver already STOPPED.");
        }
        logger.debug("Trying to stop embedded DB");
        EmbeddedDBUtil.stop();
    }
}
