package edu.yale.library.ladybird.engine.cron;


import edu.yale.library.ladybird.kernel.beans.Monitor;
import edu.yale.library.ladybird.kernel.cron.DefaultJobsManager;
import edu.yale.library.ladybird.engine.model.CronSchedulingException;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.Scheduler;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class FilePickerScheduler {
    private final Logger logger = getLogger(this.getClass());

    /**
     * Schedules an import cron job. To be called from kernel at start up.
     *
     * @param jobName
     * @param triggerName
     * @param cronExpression
     * @throws Exception
     */
    public void schedulePickJob(final String jobName, final String triggerName, String groupName,
                                String cronExpression, Monitor monitorItem) {
        logger.debug("Scheduling pick job");

        JobDetail job;
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start(); //N.B.
            //schedule a job
            job = getJob(jobName, monitorItem);
            scheduler.scheduleJob(job, getRunOnceTrigger(cronExpression, triggerName, groupName));
        } catch (SchedulerException e) {
            throw new CronSchedulingException(e);
        }

        //add to jobs manager
        DefaultJobsManager defaultJobsManager = new DefaultJobsManager();
        defaultJobsManager.addJob(job);
        logger.debug("Done scheduling pick job");
    }

    private Trigger getRunOnceTrigger(final String cronExpression, final String triggerName, final String groupName) {
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
                .build();
        return trigger;
    }

    private JobDetail getJob(String jobName, Monitor monitorItem) {
        JobDetail job = JobBuilder.newJob(FilePickerJob.class).
                withIdentity(jobName, "PICK-J").build();
        job.getJobDataMap().put("event", monitorItem); //used by FilePickerJob
        return job;
    }
}

