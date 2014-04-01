package edu.yale.library.ladybird.kernel;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import edu.yale.library.ladybird.kernel.cron.NotificationScheduler;
import edu.yale.library.ladybird.kernel.events.AbstractNotificationJob;

/**
 *
 */
public class KernelContext {

    private static Module jobModule; //TODO singleton

    /**
     * Initializes a notifiation scheduler
     * @throws Exception
     * @see #scheduleGenericJob(edu.yale.library.ladybird.kernel.events.AbstractNotificationJob, String, String)
     */
    public static void initNotificationScheduler() throws Exception {
        Injector injector = Guice.createInjector(getJobModule());
        NotificationScheduler notificationScheduler = injector.getInstance(NotificationScheduler.class);
        notificationScheduler.scheduleJob("notification", "trigger");
    }

    /**
     * Utility method for scheduling a (generic) cron job
     * @param jobName
     * @param cronExpression
     * @return
     */
    public static void scheduleGenericJob(final AbstractNotificationJob notificationJob, final String jobName,
                                          final String cronExpression) {
        Guice.createInjector(getJobModule(), new org.nnsoft.guice.guartz.QuartzModule() {
            @Override
            protected void schedule() {
                scheduleJob(notificationJob.getClass()).withCronExpression(cronExpression).withJobName(jobName);
            }
        });
    }

    private static Module getJobModule() {
        return jobModule;
    }

    public void setAbstractModule(Module abstractModule) {
        this.jobModule = abstractModule;
    }
}
