package edu.yale.library.ladybird.kernel.events;


import com.google.inject.Inject;
import edu.yale.library.ladybird.entity.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Polls the queue.
 * <p/>
 * TODO Subject to modification because the current impl. will fail silently
 * (remove the job from the queue,but fail sending it).
 */
public class NotificationJob extends AbstractNotificationJob implements Job {
    private final Logger logger = getLogger(this.getClass());

    private NotificationHandler notificationHandler;

    @Inject
    public NotificationJob(NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        NotificationEventQueue.NotificationItem notificationItem = NotificationEventQueue.getLastEvent();
        Event event = null;  //FIXME  see javadoc comment
        User user = null;
        try {
            event = notificationItem.getE();
            user = notificationItem.getUsers().get(0); //TODO could be multimpe users

        } catch (Exception e) {
            logger.trace(e.getMessage());
        }

        if (event == null || user == null) { //FIXME
            return;
        }

        try {
            logger.debug("Notifying user=" + user.toString());
            notificationHandler.notifyUser(user, event); //todo actual/more params
            logger.trace("Notification sent.");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
