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
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;

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

            logger.debug("Sending e-mail notification to user email={} with subject={}", user.getEmail(), event.getEventName());

            email.send();

        } catch (EmailException e) {
            logger.error("Exception sending notification", e.getMessage()); //TODO
        }
    }

    //TODO test @see EMailNotificationHandlerTest
    @Override
    public void notifyUserWithFile(User user, Event event, File file) {

        logger.debug("Sending e-mail notification to user email={} with subject={} with fileName={}",
                user.getEmail(), event.getEventName(), file.getName());

        final Email email = new MultiPartEmail();
        try {
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

        } catch (Exception e) {
            logger.error("Exception sending notification", e.getMessage()); //TODO
        }
    }
}
