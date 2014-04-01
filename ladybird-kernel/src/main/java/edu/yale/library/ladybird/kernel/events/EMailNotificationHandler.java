package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.kernel.beans.User;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMailNotificationHandler implements NotificationHandler {

    private final Logger logger = LoggerFactory.getLogger(EMailNotificationHandler.class);

    public void notifyUser(final User user, final Event event) {
        final Email email = new SimpleEmail();
        try {
            email.setHostName(ApplicationProperties.CONFIG_STATE.EMAIL_HOST);
            email.setSmtpPort(ApplicationProperties.CONFIG_STATE.EMAIL_PORT);
            email.setFrom(ApplicationProperties.CONFIG_STATE.EMAIL_ADMIN);
            email.setSubject(event.getEventName());
            email.setMsg(event.toString()); //TODO
            email.addTo(user.getEmail());

            logger.debug("Sending e-mail notification to user email={}", user.getEmail());

            email.send();

            logger.debug("Sent e-mail notification to user email={}", user.getEmail());
        } catch (EmailException e) {
            logger.error("Exception sending notification");
            e.printStackTrace(); //TODO
        }
    }
}
