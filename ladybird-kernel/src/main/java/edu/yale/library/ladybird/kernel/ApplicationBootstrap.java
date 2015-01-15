package edu.yale.library.ladybird.kernel;

import com.google.inject.Module;
import edu.yale.library.ladybird.kernel.cron.NotificationScheduler;
import edu.yale.library.ladybird.persistence.HibernateUtil;
import org.slf4j.Logger;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static org.apache.commons.lang.time.DurationFormatUtils.formatDuration;
import static org.slf4j.LoggerFactory.getLogger;

public class ApplicationBootstrap {

    private static final Logger logger = getLogger(ApplicationBootstrap.class);

    private static final String TIMESTAMP_FORMAT = "HH:mm:ss:SS";

    /** Time Hibernate Session Factory has been alive */
    private static long hibernateUptime = 0;

    /** Time embedded db has been alive */
    private static long dbUptime = 0;

    private final EmbeddedDBServicesManager embeddedDBServicesManager = new EmbeddedDBServicesManager();

    private static Module guiceModule;

    /**
     * Start db, init scheduler, wire in guice bindings
     */
    public void init() {
        try {
            if (!getProperty("file.encoding").equals("UTF-8")) {
                logger.error("UTF-8 file encoding not detected.");
            }

            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Starting embedded DB");
                embeddedDBServicesManager.initDB();
                dbUptime = currentTimeMillis();
                logger.debug("Started embedded DB");
            }

            hibernateUptime = HibernateUtil.getSessionFactory().getStatistics().getStartTime();
            logger.debug("Built Session Factory");

            //bootstrap notification:
            NotificationScheduler.setGuiceModule(guiceModule);
            NotificationScheduler.initNotificationScheduler();

            //init event handler:
            EventHandler.setGuiceModule(guiceModule);
        } catch (Throwable t) {
            logger.error("Error in context initialization", t);
        }
    }

    /**
     * Stop embedded db
     */
    public void stop() {
        try {
            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Stopping embedded DB");
                // TODO check DB state
                embeddedDBServicesManager.stopDB();
                logger.debug("Closed embedded db. Time={}", getElapsed(dbUptime));
            }
            HibernateUtil.shutdown();
            logger.debug("Closed Hibernate SessionFactory. Time={}",  getElapsed(hibernateUptime));
        } catch (Throwable t) {
            logger.error("Error in context shutdown", t);
        }
    }

    public void setGuiceModule(Module abstractModule) {
        if (guiceModule == null) {
            guiceModule = abstractModule;
        }
    }

    private static String getElapsed(final long startTime) {
        return formatDuration(currentTimeMillis() - startTime, TIMESTAMP_FORMAT);
    }
}
