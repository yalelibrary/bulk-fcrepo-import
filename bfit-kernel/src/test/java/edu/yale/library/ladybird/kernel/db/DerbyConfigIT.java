package edu.yale.library.ladybird.kernel.db;

import edu.yale.library.ladybird.kernel.ApplicationProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.junit.Assert.fail;

public class DerbyConfigIT {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private EmbeddedDBServicesManager embeddedDBServicesManager;

    @Before
    public void init() {
        embeddedDBServicesManager = new EmbeddedDBServicesManager();
    }

    @After
    public void shtudown() {
        try {
            embeddedDBServicesManager.stopDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldFailMultipleDriverStarting() {
        embeddedDBServicesManager.startDB();
        try {
            embeddedDBServicesManager.startDB();
            fail("Failed. Tried to re-init driver.");
        } catch (final EmbeddedDBException e) {
            if (!e.getMessage().equalsIgnoreCase("Driver already RUNNING.")) {
                logger.error("Error", e);
                fail("Failed. Tried to re-init driver.");
            }
        }
    }
}
