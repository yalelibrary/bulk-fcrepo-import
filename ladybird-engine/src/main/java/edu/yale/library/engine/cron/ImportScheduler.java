package edu.yale.library.engine.cron;


import static org.slf4j.LoggerFactory.getLogger;


import edu.yale.library.cron.DefaultJobsManager;
import edu.yale.library.engine.model.CronSchedulingException;
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
    public void scheduleJob(final String jobName, final String triggerName, String cronExpression)
    {
        logger.debug("Scheduling import job");

        JobDetail job;
        try
        {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            job = getJob(jobName, ImportJob.class);
            scheduler.scheduleJob(job, getTrigger(cronExpression));
        }
        catch (SchedulerException e)
        {
            throw new CronSchedulingException(e);
        }

        //add to jobs manager
        DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
        defaultJobsManager.addJob(job);
    }

    protected Trigger getTrigger(String cronExpression)
    {
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("IMG-TRIGER", "IMJ")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        return trigger;
    }

    protected JobDetail getJob(String jobName, Class klass)
    {
        JobDetail job = JobBuilder.newJob(klass)
                .withIdentity(jobName, "IMJ").build();
        return job;
    }

}

