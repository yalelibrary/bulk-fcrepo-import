package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.event.Event;

import java.io.File;

/**
 * @see EMailNotificationHandler for an example
 */
public interface NotificationHandler {

    void notifyUser(User user, Event event, String message, String subject);

    void notifyUserWithFile(User user, Event event, File file);

}
