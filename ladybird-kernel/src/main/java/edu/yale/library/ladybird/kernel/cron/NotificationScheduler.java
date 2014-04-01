package edu.yale.library.ladybird.kernel.cron;


import com.google.inject.Guice;
import com.google.inject.Inject;
import edu.yale.library.ladybird.kernel.JobModule;
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
     * Schedules and starts the notification job.
     *
     * @param jobName
     * @param triggerName
     * @param cronExpression
     * @throws Exception
     */
    public void scheduleJob(final String jobName, final String triggerName, String cronExpression) throws Exception {
        logger.debug("Scheduling job= {}", jobName);
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        final String scheduledJob = getJob(jobName, cronExpression);
        //add to jobs manager  //FIXME
        //DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
        //defaultJobsManager.addJob(jobName);
    }

    /**
     * Schedule job
     * @param jobName
     * @param cronExpression
     * @return
     */
    private String getJob(final String jobName, final String cronExpression) {
        Guice.createInjector(new JobModule(), new org.nnsoft.guice.guartz.QuartzModule() {
            @Override
            protected void schedule() {
                 scheduleJob(notificationJob.getClass()).withCronExpression(cronExpression).withJobName(jobName);
            }
        });
        return notificationJob.getClass().getName();
    }
}

