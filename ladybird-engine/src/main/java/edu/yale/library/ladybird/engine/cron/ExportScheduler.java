package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.kernel.cron.DefaultJobsManager;
import edu.yale.library.ladybird.engine.CronSchedulingException;
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
     * @param cronExpression
     * @throws Exception
     */
    public void scheduleJob(final String jobName, String cronExpression) {
        logger.debug("Scheduling export job");

        JobDetail job;
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            job = getJob(jobName, ExportJobFactory.getInstance().getClass());
            final Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("EXJ-TRIGER", "EXJ")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();
            doScheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
        defaultJobsManager.addJob(job);
    }


    @Deprecated
    public void scheduleJob(final String jobName, final Monitor monitorItem, final Trigger trigger) {
        logger.debug("Scheduling export job");

        JobDetail job;
        try {
            job = getJob(jobName, ExportJobFactory.getInstance().getClass(), monitorItem);
            doScheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
        defaultJobsManager.addJob(job);
    }

    private void doScheduleJob(final JobDetail job, final Trigger trigger) throws SchedulerException {
        try {
            final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw e;
        }
    }


    @SuppressWarnings("unchecked")
    @Deprecated
    protected JobDetail getJob(String jobName, Class klass, Monitor monitorItem) {
        JobDetail job = JobBuilder.newJob(klass)
                .withIdentity(jobName, "EXJ").build();
        job.getJobDataMap().put("event", monitorItem); //used by DefaultExportJob
        return job;
    }

    @SuppressWarnings("unchecked")
    protected JobDetail getJob(String jobName, Class klass) {
        JobDetail job = JobBuilder.newJob(klass)
                .withIdentity(jobName, "EXJ").build();
        return job;
    }
}
