package edu.yale.library.ladybird.kernel.cron;


import com.google.inject.Inject;
import edu.yale.library.ladybird.kernel.ApplicationBootstrap;
import edu.yale.library.ladybird.kernel.events.AbstractNotificationJob;
import edu.yale.library.ladybird.kernel.events.NotificationJob;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public final class NotificationScheduler {

    private final Logger logger = getLogger(this.getClass());

    private AbstractNotificationJob notificationJob;

    @Inject
    public NotificationScheduler(NotificationJob notificationJob) {
        this.notificationJob = notificationJob;
    }

    /**
     * Schedule job
     * TODO add to ScheduledJobs:
     * @see edu.yale.library.ladybird.kernel.cron.ScheduledJobs
     */
    public void scheduleJob(final String jobName)  throws Exception {
        logger.debug("Scheduling={}", jobName);
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        doSchedule(jobName, getNotificationCronSchedule());
    }

    public void doSchedule(final String jobName, final String cronExpression) {
        ApplicationBootstrap.scheduleGenericJob(notificationJob, jobName, cronExpression);
    }


    private static String getNotificationCronSchedule() {
        return "0/5 * * * * ?";
    }
}

