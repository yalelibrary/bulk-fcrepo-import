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
        Event event = notificationItem.getE();  //FIXME  see javadoc comment
        User user = notificationItem.getUsers().get(0); //FIXME; could be multimpe users

        if (event == null || user == null) { //FIXME
            return;
        }

        try {
            logger.debug("Notifying user=" + user.toString());
            notificationHandler.notifyUser(user, event); //todo actual/more params
            logger.trace("Notification sent.");
        } catch (Exception e) {
            e.printStackTrace(); //todo
        }
    }
}
