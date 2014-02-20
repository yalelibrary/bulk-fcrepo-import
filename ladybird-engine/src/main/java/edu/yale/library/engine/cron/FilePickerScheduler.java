package edu.yale.library.engine.cron;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class FilePickerScheduler
{
    private final Logger logger = getLogger(this.getClass());
    
    /**
     * Schedules an import cron job. To be called from kernel at start up.
     * @param jobName
     * @param triggerName
     * @param cronExpression
     * @throws Exception
     */
    public void schedulePickJob(final String jobName, final String triggerName, String groupName,
                                String cronExpression, String path) throws Exception
    {
        logger.debug("Scheduling pick job");
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();//N.B.
        //schedule a job
        JobDetail job = getJob(jobName, path);
        scheduler.scheduleJob(job, getRunOnceTrigger(cronExpression, triggerName, groupName));
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

    private JobDetail getJob(String jobName, String path) {
        JobDetail job = JobBuilder.newJob(FilePickerJob.class).
                withIdentity(jobName, "PICK-J").build();
        job.getJobDataMap().put("path", path);
        return job;
    }
}

