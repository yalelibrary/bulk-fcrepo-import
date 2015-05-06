package edu.yale.library.ladybird.kernel.cron;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public final class NotificationScheduler {

    private static final Logger logger = getLogger(NotificationScheduler.class);

    private AbstractNotificationJob notificationJob;

    private static Module guiceModule;

    @Inject
    public NotificationScheduler(NotificationJob notificationJob) {
        this.notificationJob = notificationJob;
    }

    /**
     * Utility method for scheduling a (generic) cron job
     */
    public static void scheduleGenericJob(final AbstractNotificationJob notificationJob,
                                          final String jobName,
                                          final String cronExpression) {
        Guice.createInjector(guiceModule, new org.nnsoft.guice.guartz.QuartzModule() {
            @Override
            protected void schedule() {
                scheduleJob(notificationJob.getClass()).withCronExpression(cronExpression)
                        .withJobName(jobName);
            }
        });
    }

    /**
     * Initializes a notification scheduler
     * @throws Exception
     */
    public static void initNotificationScheduler() throws Exception {
        final Injector injector = Guice.createInjector(guiceModule);
        NotificationScheduler notificationScheduler = injector.getInstance(NotificationScheduler.class);
        notificationScheduler.doSchedule("notification");
    }

    /**
     * Schedule job
     * TODO add to:
     * @see edu.yale.library.ladybird.kernel.cron.ScheduledJobs
     */
    public void doSchedule(final String jobName)  throws Exception {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        logger.debug("Scheduling={}", jobName);
        scheduler.start();
        scheduleGenericJob(notificationJob, jobName, getNotificationCronSchedule());
    }

    public static void setGuiceModule(Module abstractModule) {
        if (guiceModule != null) {
            logger.error("Already inst");
        }
        guiceModule = abstractModule;
    }

    //TODO
    private String getNotificationCronSchedule() {
        return "0/5 * * * * ?";
    }
}

