package edu.yale.library.engine.cron;


import static org.slf4j.LoggerFactory.getLogger;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

public class ImportScheduler
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
    public void scheduleImportJob(final String jobName, final String triggerName, String cronExpression) throws Exception {
        logger.debug("Scheduling import job");
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
                .withIdentity("IMG-TRIGER", "IMJ")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        return trigger;
    }

    private JobDetail getJob(String jobName) {
        JobDetail job = JobBuilder.newJob(ImportJob.class)
                .withIdentity(jobName, "IMJ").build();
        return job;
    }
}

