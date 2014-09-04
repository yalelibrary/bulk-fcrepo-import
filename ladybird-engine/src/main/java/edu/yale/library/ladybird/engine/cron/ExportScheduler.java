package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.CronSchedulingException;
import edu.yale.library.ladybird.engine.imports.ImportEngineException;
import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.kernel.cron.DefaultJobsManager;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ExportScheduler {
    private final Logger logger = getLogger(this.getClass());

    private static final String DEFAULT_GROUP = "EXJ";
    private static final String DEFAULT_EXPORT_JOB_ID = "export_job";

    /**
     * Schedules an export cron job. To be called from kernel at start up.
     *
     * @param cronExpression
     * @throws Exception
     */
    public void scheduleJob(String cronExpression) {
        logger.debug("Scheduling export job.");

        JobDetail job;
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            job = getJob(DEFAULT_EXPORT_JOB_ID, ExportJobFactory.getInstance().getClass());
            final Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("EXJ-TRIGER", DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();
            doScheduleJob(job, trigger);

            DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
            defaultJobsManager.addJob(job);
        } catch (SchedulerException e) {
            logger.error("Error", e);
            throw new CronSchedulingException(e);
        } catch (Exception e){
            logger.error("Error", e);
        }
    }

    @Deprecated
    public void scheduleJob(final Monitor monitorItem, final Trigger trigger) {
        logger.debug("Scheduling export job..");

        JobDetail job;
        try {
            job = getJob(DEFAULT_EXPORT_JOB_ID, ExportJobFactory.getInstance().getClass(), monitorItem);
            doScheduleJob(job, trigger);

            DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
            defaultJobsManager.addJob(job);
        } catch (SchedulerException e) {
            logger.error("Error", e);
            throw new CronSchedulingException(e);
        }
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

    public void cancel() {
        doCancel();
    }

    private void doCancel() {
        try {
            final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            final Trigger existingTrigger = scheduler.getTrigger(new TriggerKey("EXJ-TRIGER", DEFAULT_GROUP));
            logger.debug("Unscheduling jobs for trigger={}", existingTrigger.getKey());
            scheduler.unscheduleJob(existingTrigger.getKey());
        } catch (SchedulerException e) {
            logger.error("Error unscheduling job", e);
            throw new CronSchedulingException(e);
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

    /** used for unscheduling */
    public static String getExportJobIdentifier() {
        return ExportScheduler.DEFAULT_GROUP + "."  + ExportScheduler.DEFAULT_EXPORT_JOB_ID;
    }

    public static String getDefaultGroup() {
        return DEFAULT_GROUP;
    }

    public static String getDefaultExportJobId() {
        return DEFAULT_EXPORT_JOB_ID;
    }
}
