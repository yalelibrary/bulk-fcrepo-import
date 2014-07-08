package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.entity.User;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DummyEMailNotificationHandler implements NotificationHandler {

    private final Logger logger = LoggerFactory.getLogger(DummyEMailNotificationHandler.class);

    public void notifyUser(final User user, final Event event) {
        final Email email = new SimpleEmail();
        try {
            email.setHostName(ApplicationProperties.CONFIG_STATE.EMAIL_HOST);
            email.setSmtpPort(ApplicationProperties.CONFIG_STATE.EMAIL_PORT);
            email.setFrom(ApplicationProperties.CONFIG_STATE.EMAIL_ADMIN);
            email.setSubject("Test");
            email.setMsg("Test Message");
            email.addTo(ApplicationProperties.CONFIG_STATE.EMAIL_ADMIN);
            email.send();
        } catch (EmailException e) {
            logger.error("Exception sending notification");
            e.printStackTrace(); //TODO
        }
    }

    @Override
    public void notifyUserWithFile(User user, Event event, File file) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
