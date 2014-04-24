package edu.yale.library.ladybird.kernel;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import edu.yale.library.ladybird.kernel.cron.NotificationScheduler;
import edu.yale.library.ladybird.kernel.events.AbstractNotificationJob;
import edu.yale.library.ladybird.persistence.HibernateUtil;
import org.apache.commons.lang.time.DurationFormatUtils;

public class KernelBootstrap {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KernelBootstrap.class);

    /** Time Hibernate Session Factory has been alive */
    private static long HIBERNATE_UPTIME = 0;
    /** Time embedded db has been alive */
    private static long DB_UPTIME = 0;

    private final ServicesManager servicesManager = new ServicesManager();

    private static Module jobModule;

    public void init() {
        logger.debug("Application Start up.");
        try {
            if (System.getProperty("file.encoding").equals("UTF-8")) {
                logger.warn("UTF-8 file encoding not detected.");
            }

            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to start embedded DB");
                servicesManager.initDB();
                DB_UPTIME = System.currentTimeMillis();
                logger.debug("Started embedded DB");
            }
            HIBERNATE_UPTIME = HibernateUtil.getSessionFactory().getStatistics().getStartTime();
            logger.debug("Built Session Factory");

            //bootstrap notification:
            final KernelBootstrap kernelContext = new KernelBootstrap();
            kernelContext.setAbstractModule(new JobModule());
            initNotificationScheduler();

        } catch (Throwable t) {
            logger.error("Error in context initialization", t);
        }
    }

    public void stop() {
        try {
            //TODO ensure DB is running
            if (ApplicationProperties.CONFIG_STATE.DEFAULT_DB_CONFIGURED) {
                logger.debug("Trying to stop embedded DB");
                servicesManager.stopDB();
                logger.debug("Closed embedded database. Time: " + getTime(DB_UPTIME));
            }
            HibernateUtil.shutdown();
            logger.debug("Closed Hibernate Session Factory. Time: " + getTime(HIBERNATE_UPTIME));
        } catch (Throwable t) {
            logger.error("Error in context shutdown", t);
        }
    }

    private String getTime(final long startTime) {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - startTime, "HH:mm:ss:SS");
    }

    /**
     * Initializes a notifiation scheduler
     * @throws Exception
     * @see #scheduleGenericJob(edu.yale.library.ladybird.kernel.events.AbstractNotificationJob, String, String)
     */
    public static void initNotificationScheduler() throws Exception {
        Injector injector = Guice.createInjector(getJobModule());
        NotificationScheduler notificationScheduler = injector.getInstance(NotificationScheduler.class);
        notificationScheduler.scheduleJob("notification", "trigger");
    }

    /**
     * Utility method for scheduling a (generic) cron job
     * @param jobName
     * @param cronExpression
     * @return
     */
    public static void scheduleGenericJob(final AbstractNotificationJob notificationJob, final String jobName,
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

}
