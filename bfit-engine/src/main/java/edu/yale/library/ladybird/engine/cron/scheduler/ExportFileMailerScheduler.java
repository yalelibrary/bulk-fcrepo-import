package edu.yale.library.ladybird.engine.cron.scheduler;

import edu.yale.library.ladybird.engine.cron.ExportMailerFactory;
import edu.yale.library.ladybird.kernel.cron.ScheduledJobsList;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ExportFileMailerScheduler {

    private final Logger logger = getLogger(this.getClass());

    private static final String DEFAULT_GROUP = "EX-MAILER";

    private static final String DEFAULT_JOB_ID = "ex-mailer-job";

    /**
     * Schedules an export cron job. To be called from kernel at start up.
     *
     * @param cronExpression expression
     */
    public void scheduleJob(String cronExpression) {
        logger.debug("Scheduling file mailer export job");

        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            JobDetail job = getJob(DEFAULT_JOB_ID, ExportMailerFactory.getInstance().getClass());
            final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("EX-MAILER-TRIGER", DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            doScheduleJob(job, trigger);

            ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
            defaultJobsManager.addJob(job);
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }
    }

    private void doScheduleJob(final JobDetail job, final Trigger trigger) throws SchedulerException {
        final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    @SuppressWarnings("unchecked")
    protected JobDetail getJob(String jobName, Class klass) {
         return JobBuilder.newJob(klass).withIdentity(jobName, DEFAULT_GROUP).build();
    }

    public static String getDefaultJobId() {
        return DEFAULT_JOB_ID;
    }
}
