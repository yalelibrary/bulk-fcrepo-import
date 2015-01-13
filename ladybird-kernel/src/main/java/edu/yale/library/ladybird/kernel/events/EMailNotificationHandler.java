package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.entity.User;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;

public class EMailNotificationHandler implements NotificationHandler {

    private final Logger logger = LoggerFactory.getLogger(EMailNotificationHandler.class);

    public void notifyUser(final User user, final Event event, String message, String subject) {
        logger.trace("Sending e-mail notification to user email={} with subject={}",
                user.getEmail(), event.getEventName());

        try {
            final Email email = new SimpleEmail();
            email.setHostName(ApplicationProperties.CONFIG_STATE.EMAIL_HOST);
            email.setSmtpPort(ApplicationProperties.CONFIG_STATE.EMAIL_PORT);
            email.setFrom(ApplicationProperties.CONFIG_STATE.EMAIL_ADMIN);
            email.setSubject(subject);
            email.setMsg(message); //TODO
            email.addTo(user.getEmail());
            email.send();
            logger.trace("Notification sent to user={}", user.getEmail());
        } catch (EmailException e) {
            logger.error("Exception sending notification", e);
        }
    }

    @Override
    public void notifyUserWithFile(User user, Event event, File file) {
        logger.trace("Sending e-mail notification to user email={} with subject={} with fileName={}",
                user.getEmail(), event.getEventName(), file.getName());

        try {
            final Email email = new MultiPartEmail();
            email.setHostName(ApplicationProperties.CONFIG_STATE.EMAIL_HOST);
            email.setSmtpPort(ApplicationProperties.CONFIG_STATE.EMAIL_PORT);
            email.setFrom(ApplicationProperties.CONFIG_STATE.EMAIL_ADMIN);
            email.setSubject(event.getEventName());
            email.setMsg(event.toString()); //TODO
            email.addTo(user.getEmail());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setFileName(file.getName());
            DataSource dataSource = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            email.setContent(multipart);
            email.send();
            logger.trace("Notification sent to user={}", user.getEmail());
        } catch (Exception e) {
            logger.error("Exception sending notification", e);
        }
    }
}
