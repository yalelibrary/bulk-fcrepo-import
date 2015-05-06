package edu.yale.library.ladybird.engine.cron.scheduler;


import edu.yale.library.ladybird.engine.cron.job.DefaultImportContextJob;
import edu.yale.library.ladybird.engine.cron.job.DefaultObjectMetadataWriterJob;
import edu.yale.library.ladybird.engine.cron.ImportJobFactory;
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

        final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("IMG-TRIGER", DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

        try {
            final JobDetail importJob = getJob(DEFAULT_IMPORT_JOB_ID, ImportJobFactory.getInstance().getClass());
            schedule(importJob, trigger);

            ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
            defaultJobsManager.addJob(importJob);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        //schedule internal jobs (these run at the same schedule/trigger as above)

        //1. importcontextreader job (this is what populates exportWriterQ and objectWriterQ
        logger.debug("Scheduling import context job");
        final Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("IMG2-TRIGER", DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

        try {
            //prep. for export
            final JobDetail importContextJob = getJob("import_context_job", DefaultImportContextJob.class);
            schedule(importContextJob, trigger2);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        //2. object writer job job (this is what will kick off the object writer job
        logger.debug("Scheduling object writer job");
        final Trigger trigger3 = TriggerBuilder.newTrigger().withIdentity("IMG3-TRIGER", DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

        try {
            final JobDetail objectWriterJob = getJob("object_writer_job", DefaultObjectMetadataWriterJob.class);
            schedule(objectWriterJob, trigger3);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }
    }

    /**
     * Used in test
     *
     * @param trigger A custom trigger
     */
    @Deprecated
    public void scheduleJob(final Trigger trigger) {
        try {
            JobDetail job = getJob(DEFAULT_IMPORT_JOB_ID, ImportJobFactory.getInstance().getClass());
            schedule(job, trigger);
            ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
            defaultJobsManager.addJob(job);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }
    }

    private void schedule(final JobDetail job, final Trigger trigger) throws SchedulerException {
        final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    public void cancel() {
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
        return JobBuilder.newJob(klass).withIdentity(jobName, DEFAULT_GROUP).build();
    }

    /**
     * Used for unscheduling
     */
    public static String getImportJobIdentifier() {
        return ImportScheduler.DEFAULT_GROUP + "." + ImportScheduler.DEFAULT_IMPORT_JOB_ID;
    }

    public static String getDefaultGroup() {
        return DEFAULT_GROUP;
    }

}

