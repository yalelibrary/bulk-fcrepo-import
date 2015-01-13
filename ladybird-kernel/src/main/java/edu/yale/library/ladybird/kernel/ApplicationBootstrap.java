package edu.yale.library.ladybird.kernel;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import edu.yale.library.ladybird.kernel.cron.NotificationScheduler;
import edu.yale.library.ladybird.kernel.events.AbstractNotificationJob;
import edu.yale.library.ladybird.kernel.events.Event;
import edu.yale.library.ladybird.persistence.HibernateUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ApplicationBootstrap {

    private static final Logger logger = getLogger(ApplicationBootstrap.class);

    private static final String TIMESTAMP_FORMAT = "HH:mm:ss:SS";

    /** Time Hibernate Session Factory has been alive */
    private static long hibernateUptime = 0;

    /** Time embedded db has been alive */
    private static long dbUptime = 0;

    private final EmbeddedDBServicesManager embeddedDBServicesManager = new EmbeddedDBServicesManager();

    private static Module jobModule;

    private static EventBus eventBus;

    public void init() {
        try {
            if (!System.getProperty("file.encoding").equals("UTF-8")) {
                logger.error("UTF-8 file encoding not detected.");
            }

            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to start embedded DB");
                embeddedDBServicesManager.initDB();
                dbUptime = System.currentTimeMillis();
                logger.debug("Started embedded DB");
            }

            hibernateUptime = HibernateUtil.getSessionFactory().getStatistics().getStartTime();
            logger.debug("Built Session Factory");

            //bootstrap notification:
            final ApplicationBootstrap kernelContext = new ApplicationBootstrap();
            kernelContext.setAbstractModule(new GuiceModule());
            initNotificationScheduler();

            if (eventBus == null) {
                eventBus = getEventBus();
            }
        } catch (Throwable t) {
            logger.error("Error in context initialization", t);
        }
    }

    /**
     * Stop embedded db
     * TODO ensure DB is running
     */
    public void stop() {
        try {
            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to stop embedded DB");
                embeddedDBServicesManager.stopDB();
                logger.debug("Closed embedded database. Time: " + getElapsedTime(dbUptime));
            }
            HibernateUtil.shutdown();
            logger.debug("Closed Hibernate Session Factory. Time: " + getElapsedTime(hibernateUptime));
        } catch (Throwable t) {
            logger.error("Error in context shutdown", t);
        }
    }

    /**
     * Initializes a notification scheduler
     * @throws Exception
     * @see #scheduleGenericJob(edu.yale.library.ladybird.kernel.events.AbstractNotificationJob, String, String)
     */
    public static void initNotificationScheduler() throws Exception {
        final Injector injector = Guice.createInjector(getJobModule());
        NotificationScheduler notificationScheduler = injector.getInstance(NotificationScheduler.class);
        notificationScheduler.scheduleJob("notification");
    }

    /**
     * Utility method for scheduling a (generic) cron job
     */
    public static void scheduleGenericJob(final AbstractNotificationJob notificationJob,
                                          final String jobName,
                                          final String cronExpression) {
        Guice.createInjector(getJobModule(), new org.nnsoft.guice.guartz.QuartzModule() {
            @Override
            protected void schedule() {
                scheduleJob(notificationJob.getClass()).withCronExpression(cronExpression).withJobName(jobName);
            }
        });
    }

    private static Module getJobModule() {
        return jobModule;
    }

    public void setAbstractModule(Module abstractModule) {
        this.jobModule = abstractModule;
    }

    /**
     * Get the event bus and instantiate listeners
     * @return
     */
    private static EventBus getEventBus() {
        final Injector injector = Guice.createInjector(getJobModule()); //TODO

        if (eventBus == null) {
            logger.debug("Inst. EventBus");
            eventBus = new EventBus();
            final List<Class> globalListeners = injector.getInstance(List.class);
            for (Class o: globalListeners) {
                eventBus.register(injector.getInstance(o));
            }
        }
        return eventBus;
    }

    public static void postEvent(final Event event) {
        getEventBus().post(event);
    }

    public static String getElapsedTime(final long startTime) {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, TIMESTAMP_FORMAT);
    }
}
