package edu.yale.library.ladybird.engine.cron.scheduler;

import edu.yale.library.ladybird.engine.cron.ImageConversionJobFactory;
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

public class ImageConversionScheduler {

    private final Logger logger = getLogger(this.getClass());

    private static final String DEFAULT_GROUP = "IMAGE-CONVERTER-GROUP";

    private static final String DEFAULT_JOB_ID = "image-converter-job";

    private static final String DEFAULT_TRIGGER = "import-converter-trigger";

    public void scheduleJob(String cronExpression) {
        logger.debug("Scheduling file mailer export job");

        try {
            final JobDetail job = getJob(DEFAULT_JOB_ID, ImageConversionJobFactory.getInstance().getClass());

            final Trigger trigger = TriggerBuilder.newTrigger().withIdentity(DEFAULT_TRIGGER, DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            doScheduleJob(job, trigger);

            ScheduledJobsList defaultJobsManager = new ScheduledJobsList();
            defaultJobsManager.addJob(job);
        } catch (Exception e) {
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
