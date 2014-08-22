package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.ladybird.entity.User;

import java.io.File;

/**
 * Supertype.
 *
 * @see EMailNotificationHandler for an example
 */
public interface NotificationHandler {
    void notifyUser(User user, Event event, String message, String subject);

    void notifyUserWithFile(User user, Event event, File file);

}
