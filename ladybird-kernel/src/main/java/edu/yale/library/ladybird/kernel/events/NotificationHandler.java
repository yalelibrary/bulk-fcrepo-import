package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.entity.model.User;

/**
 * Supertype.
 *
 * @see EMailNotificationHandler for an example
 */
public interface NotificationHandler {
    void notifyUser(User user, Event event);
}
