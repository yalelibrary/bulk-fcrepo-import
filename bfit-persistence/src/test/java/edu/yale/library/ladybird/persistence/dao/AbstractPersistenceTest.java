package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.persistence.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 *
 */
public class AbstractPersistenceTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPersistenceTest.class);

    private static final ServicesManager servicesManager = new ServicesManager();
    private static boolean containerStarted = false;

    public static void initDB() {
        if (containerStarted) {
            return;
        }
        try {
            servicesManager.initDB();
            containerStarted = true;
        } catch (Exception e) {
            logger.error("Error init DB", e);
        }
    }

    public static void stopDB() throws SQLException {
        if (!containerStarted) {
            return;
        }
        try {
            servicesManager.stopDB();
        } catch (SQLException e) {
            logger.error("Error stop DB", e);
        }
    }

}
