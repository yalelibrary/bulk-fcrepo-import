package edu.yale.library.ladybird.engine.cron.scheduler;

import edu.yale.library.ladybird.engine.cron.ExportJobFactory;
import edu.yale.library.ladybird.kernel.cron.ScheduledJobsList;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
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
     */
    public void scheduleJob(String cronExpression) {
        logger.debug("Scheduling export job.");

        try {
            JobDetail job = getJob(DEFAULT_EXPORT_JOB_ID, ExportJobFactory.getInstance().getClass());
            final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("EXJ-TRIGER", DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            doScheduleJob(job, trigger);

            ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
            defaultJobsManager.addJob(job);
        } catch (SchedulerException e) {
            logger.error("Error", e);
            throw new CronSchedulingException(e);
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }

    /**
     * Used by test
     */
    @Deprecated
    public void scheduleJob(final Trigger trigger) {
        logger.debug("Scheduling export job..");

        JobDetail job;
        try {
            job = getJob(DEFAULT_EXPORT_JOB_ID, ExportJobFactory.getInstance().getClass());
            doScheduleJob(job, trigger);

            ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
            defaultJobsManager.addJob(job);
        } catch (SchedulerException e) {
            logger.error("Error", e);
            throw new CronSchedulingException(e);
        }
    }

    private void doScheduleJob(final JobDetail job, final Trigger trigger) throws SchedulerException {
        final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    public void cancel() {
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
    protected JobDetail getJob(String jobName, Class klass) {
        return JobBuilder.newJob(klass).withIdentity(jobName, "EXJ").build();
    }

    /** used for unscheduling */
    public static String getExportJobIdentifier() {
        return ExportScheduler.DEFAULT_GROUP + "."  + ExportScheduler.DEFAULT_EXPORT_JOB_ID;
    }

    public static String getDefaultExportJobId() {
        return DEFAULT_EXPORT_JOB_ID;
    }
}
