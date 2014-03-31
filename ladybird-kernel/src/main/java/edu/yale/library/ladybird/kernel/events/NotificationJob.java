package edu.yale.library.ladybird.kernel.events;


import edu.yale.library.ladybird.kernel.beans.User;
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
public class NotificationJob implements Job {
    private final Logger logger = getLogger(this.getClass());

    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        NotificationEventQueue.NotificationItem notificationItem = NotificationEventQueue.getLastEvent();
        Event event = notificationItem.getE();  //FIXME  see javadoc comment
        User user = notificationItem.getUsers().get(0); //FIXME; could be multimpe users

        if (event == null || user == null) {
            return;
        }

        try {
            logger.debug("Notifying user=" + user.toString());
            NotificationHandler notificationHandler = new EMailNotificationHandler();
            notificationHandler.notifyUser(user, event); //todo actual/more params
            logger.debug("Notification sent.");
        } catch (Exception e) {
            e.printStackTrace(); //todo
        }
    }
}
