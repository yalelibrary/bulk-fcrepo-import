package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.kernel.beans.Monitor;
import edu.yale.library.ladybird.kernel.cron.DefaultJobsManager;
import edu.yale.library.ladybird.engine.model.CronSchedulingException;
import org.quartz.JobDetail;
import org.quartz.CronScheduleBuilder;
import org.quartz.SchedulerException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.JobBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ExportScheduler {
    private final Logger logger = getLogger(this.getClass());

    /**
     * Schedules an export cron job. To be called from kernel at start up.
     *
     * @param jobName
     * @param triggerName
     * @param cronExpression
     * @throws Exception
     */
    public void scheduleJob(final String jobName, final String triggerName, String cronExpression, Monitor monitorItem) {
        logger.debug("Scheduling export job");

        JobDetail job;
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            job = getJob(jobName, ExportJob.class, monitorItem);
            scheduler.scheduleJob(job, getTrigger(cronExpression));
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }
        //add to jobs manager
        DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
        defaultJobsManager.addJob(job);
    }

    protected Trigger getTrigger(String cronExpression) {
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("EXJ-TRIGER", "EXJ")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        return trigger;
    }

    @SuppressWarnings("unchecked")
    protected JobDetail getJob(String jobName, Class klass, Monitor monitorItem) {
        JobDetail job = JobBuilder.newJob(klass)
                .withIdentity(jobName, "EXJ").build();
        job.getJobDataMap().put("event", monitorItem); //used by ExportJob
        return job;
    }
}
