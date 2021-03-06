package edu.yale.library.ladybird.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public final class ServicesManager {
    private static final Logger logger = LoggerFactory.getLogger(ServicesManager.class);

    public void initDB() {
        startDB();
        EmbeddedDatabaseUtil.init();
    }

    public void startDB() {
        if (EmbeddedDatabaseUtil.isDatabaseRunning()) {
            throw new RuntimeException(ApplicationProperties.ALREADY_RUNNING);
        }
        logger.debug("Trying to start and init DB");
        EmbeddedDatabaseUtil.start();
    }

    public void stopDB() throws SQLException {
        if (!EmbeddedDatabaseUtil.isDatabaseRunning()) {
            throw new RuntimeException(ApplicationProperties.ALREADY_STOPPED);
        }
        logger.debug("Trying to stop DB");
        EmbeddedDatabaseUtil.stop();
    }
}
