package edu.yale.library.events;

import edu.yale.library.beans.User;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General email utility
 * TODO read port etc from properties.
 * TODO decide on system "from" email, and subject of e-mails
 */
public class EMailNotificationHandler implements NotificationHandler {
    private static final int SMTP_PORT = 587;
    private static final String HOST_NAME = "mail.yale.edu";

    private Logger logger = LoggerFactory.getLogger(EMailNotificationHandler.class);

    public void notifyUser(User user, Event event) {
        Email email = new SimpleEmail();
        try {
            email.setHostName(HOST_NAME);
            email.setSmtpPort(SMTP_PORT);
            email.setFrom(user.getEmail()); //todo should be some sys email
            email.setSubject("LadyBird Test E-mail"); //?
            email.setMsg(event.toString()); //?
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
