package edu.yale.library.ladybird.kernel;

import edu.yale.library.ladybird.kernel.db.EmbeddedDBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public final class EmbeddedDBServicesManager {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedDBServicesManager.class);

    public void initDB() {
        startDB();
        EmbeddedDBUtil.init();
    }

    public void startDB() {
        if (EmbeddedDBUtil.isDatabaseRunning()) {
            throw new EmbeddedDBException(ApplicationProperties.ALREADY_RUNNING);
        }
        logger.debug("Trying to start and init embedded DB");
        EmbeddedDBUtil.start();
    }

    public void stopDB() throws SQLException {
        if (!EmbeddedDBUtil.isDatabaseRunning()) {
            throw new EmbeddedDBException(ApplicationProperties.ALREADY_STOPPED);
        }
        logger.debug("Trying to stop embedded DB");
        EmbeddedDBUtil.stop();
    }
}
