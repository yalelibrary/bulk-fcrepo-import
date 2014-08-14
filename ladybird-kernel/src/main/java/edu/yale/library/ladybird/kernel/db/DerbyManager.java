package edu.yale.library.ladybird.kernel.db;

import edu.yale.library.ladybird.kernel.AppConfigException;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class DerbyManager {
    private static final Logger logger = LoggerFactory.getLogger(DerbyManager.class);

    private static volatile Boolean RUNNING = false;

    private static final DerbyManager INSTANCE = new DerbyManager();

    private DerbyManager() {
    }

    public boolean isRUNNING() {
        return RUNNING;
    }

    public static DerbyManager getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Stop database.
     */
    protected synchronized void stop() throws SQLException {
        synchronized (RUNNING) {
            if (!RUNNING) {
                logger.debug("Not running already.");
                return;
            }
            doStop();
            RUNNING = false;
        }
    }

    private synchronized void doStop() throws SQLException {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            if (abnormalShutdown(e)) {
                logger.error("DB did not shut down normally", e);
            }
        }
    }

    /**
     * Start Db
     *
     * @return
     * @throws edu.yale.library.ladybird.kernel.AppConfigException
     */
    protected synchronized void start() {
        synchronized (RUNNING) {
            if (RUNNING) {
                throw new AppConfigException(ApplicationProperties.ALREADY_RUNNING);
            }
            try {
                doStart();
                logger.debug("Started driver");
                RUNNING = true;
            } catch (AppConfigException e) {
                throw new AppConfigException(e);
            }
        }
    }

    private synchronized void doStart() {
        try {
            loadDriver();
        } catch (Exception e) {
            throw new AppConfigException(e);
        }
    }

    private boolean abnormalShutdown(SQLException e) {
        return (((e.getErrorCode() != Config.ERROR_CODE)
                && (!Config.ABNORMAL_STATE.equals(e.getSQLState()))))
                ? true : false;
    }

    /**
     * Loads db driver.
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private void loadDriver() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class.forName(Config.DRIVER).newInstance();
    }

    /**
     * General config settings
     */
    private static final class Config {
        static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

        static final String ABNORMAL_STATE = "XJ015";

        static final int ERROR_CODE = 50000;

        private Config() {
        }
    }
}