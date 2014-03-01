package edu.yale.library.events;

import edu.yale.library.beans.User;

/**
 * Supertype.
 *
 * @see EMailNotificationHandler for an example
 */
public interface NotificationHandler {
    void notifyUser(User user, Event event);
}
