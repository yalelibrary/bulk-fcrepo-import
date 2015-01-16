package edu.yale.library.ladybird.kernel.cron;


import com.google.inject.Inject;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.event.Event;
import edu.yale.library.ladybird.kernel.notificaiton.NotificationEventQueue;
import edu.yale.library.ladybird.kernel.notificaiton.NotificationHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class NotificationJob extends AbstractNotificationJob implements Job {

    private final Logger logger = getLogger(this.getClass());

    private NotificationHandler notificationHandler;

    @Inject
    public NotificationJob(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    /**
     * FIXME Current impl. will fail silently (remove the job from Q but fail to send it)
     * @param ctx JobExecutionContext
     * @throws JobExecutionException
     */
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        final NotificationEventQueue.NotificationItem notificationItem
                = NotificationEventQueue.getLastEvent();

        try {
            final Event event = notificationItem.getEvent();
            final User user = notificationItem.getUsers().get(0); //TODO multimpe users?

            if (event == null || user == null) {
                return;
            }

            logger.trace("Notifying user={} for event={}", user.getUserId(), event.getEventName());
            notificationHandler.notifyUser(user, event,
                    notificationItem.getMessage(), notificationItem.getSubject());
        } catch (Exception e) {
            logger.error("Error executing notification job", e);
        }
    }
}
