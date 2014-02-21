package edu.yale.library.cron;


import edu.yale.library.events.NotificationJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public final class NotificationScheduler
{

    private final Logger logger = getLogger(this.getClass());

    /**
     * Schedules an import cron job. To be called from kernel at start up.
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
        JobDetail job = getJob(jobName);
        scheduler.scheduleJob(job, getTrigger(cronExpression));
        //add to jobs manager
        DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
        defaultJobsManager.addJob(job);
    }

    private Trigger getTrigger(String cronExpression) {
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("NOT-TRIGGER", "NOT")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        return trigger;
    }

    private JobDetail getJob(String jobName) {
        JobDetail job = JobBuilder.newJob(NotificationJob.class).
                withIdentity(jobName, "NOT-J").build();
        return job;
    }
}

