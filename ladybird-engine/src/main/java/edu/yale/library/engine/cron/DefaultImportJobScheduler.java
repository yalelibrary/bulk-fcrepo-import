package edu.yale.library.engine.cron;


import static org.slf4j.LoggerFactory.getLogger;


import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

public class DefaultImportJobScheduler
{
    private final Logger logger = getLogger(this.getClass());
    
    /**
     * Schedules an import cron job
     * @param jobName
     * @param triggerName
     * @param cronExpression
     * @throws Exception
     */
    public void scheduleNewJob(final String jobName, final String triggerName, String cronExpression) throws Exception
    {
        logger.debug("Scheduling job");
    }
}

