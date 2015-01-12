package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.engine.CronSchedulingException;
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

public class ImportScheduler {

    private final Logger logger = getLogger(this.getClass());

    private static final String DEFAULT_GROUP = "IMJ";

    private static final String DEFAULT_IMPORT_JOB_ID = "import_job";

    /**
     * Schedules an import cron job.
     */
    public void scheduleJob(final String cronExpression) {
        logger.debug("Scheduling import job");

        final Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("IMG-TRIGER", DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        final JobDetail importJob;
        try {
            importJob = getJob(DEFAULT_IMPORT_JOB_ID, ImportJobFactory.getInstance().getClass());

            doScheduleJob(importJob, trigger);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
        defaultJobsManager.addJob(importJob);

        //schedule internal jobs (these run at the same schedule/trigger as above)

        //1. importcontextreader job (this is what populates exportWriterQ and objectWriterQ
        final Trigger trigger2 = TriggerBuilder
                .newTrigger()
                .withIdentity("IMG2-TRIGER", DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        final JobDetail importContextJob;

        try {
            importContextJob = getJob("import_context_job", DefaultImportContextJob.class); //prep. for export
            doScheduleJob(importContextJob, trigger2);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        //2. object writer job job (this is what will kick off the object writer job
        final Trigger trigger3 = TriggerBuilder
                .newTrigger()
                .withIdentity("IMG3-TRIGER", DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        final JobDetail objectWriterJob;

        try {
            objectWriterJob = getJob("object_writer_job", DefaultObjectMetadataWriterJob.class);
            doScheduleJob(objectWriterJob, trigger3);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }
    }

    @Deprecated
    public void scheduleJob(final Trigger trigger) {
        logger.debug("Scheduling import job");

        JobDetail job;
        try {
            job = getJob(DEFAULT_IMPORT_JOB_ID, ImportJobFactory.getInstance().getClass());
            doScheduleJob(job, trigger);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
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

    public void cancel() {
        doCancel();
    }

    private void doCancel() {
        try {
            final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            final Trigger existingTrigger = scheduler.getTrigger(new TriggerKey("IMG-TRIGER", DEFAULT_GROUP));
            logger.debug("Unscheduling jobs for trigger={}", existingTrigger.getKey());
            scheduler.unscheduleJob(existingTrigger.getKey());
        } catch (SchedulerException e) {
            logger.error("Error unscheduling job", e);
            throw new CronSchedulingException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected JobDetail getJob(String jobName, Class klass) {
        JobDetail job = JobBuilder.newJob(klass)
                .withIdentity(jobName, "IMJ").build();
        return job;
    }

    /** currently being used for unscheduling */
    public static String getImportJobIdentifier() {
        return ImportScheduler.DEFAULT_GROUP + "."  + ImportScheduler.DEFAULT_IMPORT_JOB_ID;
    }

    public static String getDefaultGroup() {
        return DEFAULT_GROUP;
    }

}

