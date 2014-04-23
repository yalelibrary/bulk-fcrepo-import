package edu.yale.library.ladybird.kernel.derby;

import edu.yale.library.ladybird.kernel.AppConfigException;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.ServicesManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static org.junit.Assert.fail;

public class DerbyConfigIT {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServicesManager servicesManager;

    @Before
    public void init() {
        servicesManager = new ServicesManager();
    }

    @After
    public void shtudown() {
        try {
            servicesManager.stopDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldFailMultipleDriverStarting() {
        servicesManager.startDB();
        try {
            servicesManager.startDB();
            fail("Failed. Tried to re-init driver.");
        } catch (final AppConfigException e) {
            if (!e.getMessage().equalsIgnoreCase(ApplicationProperties.ALREADY_RUNNING)) {
                logger.error("Error", e);
                fail("Failed. Tried to re-init driver.");
            }
        }
    }
}
