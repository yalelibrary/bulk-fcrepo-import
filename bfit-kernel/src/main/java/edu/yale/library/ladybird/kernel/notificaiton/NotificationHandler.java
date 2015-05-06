package edu.yale.library.ladybird.kernel.notificaiton;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.event.Event;

import java.io.File;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface NotificationHandler {

    void notifyUser(User user, Event event, String message, String subject);

    void notifyUserWithFile(User user, Event event, File file);

}
