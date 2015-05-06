package edu.yale.library.ladybird.kernel.events;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.UserBuilder;
import edu.yale.library.ladybird.entity.event.ImportEvent;
import edu.yale.library.ladybird.kernel.notificaiton.EMailNotificationHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

public class EMailNotificationHandlerTest {
    private Logger logger = LoggerFactory.getLogger(EMailNotificationHandler.class);

    private static final int TEST_PORT = 8082; //TODO

    @Test
    public void testSend() {
        final SimpleSmtpServer server = SimpleSmtpServer.start(TEST_PORT);
        logger.debug("Started server at={}", TEST_PORT);
        final ImportEvent testEvent = new ImportEvent();

        try {
            final EMailNotificationHandler notificationHandler = new EMailNotificationHandler();
            final User user = new UserBuilder().createUser();
            user.setEmail("test@test.edu");
            notificationHandler.notifyUser(user, testEvent, "empty message", "Subject");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception= " + e);
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                fail("Error stopping server.");
            }
        }

        assertTrue(server.getReceivedEmailSize() == 1);
        final Iterator emailIter = server.getReceivedEmail();
        final SmtpMessage email = (SmtpMessage) emailIter.next();
        assertEquals("Wrong subject", email.getHeaderValue("Subject"), "Subject");
        assertEquals("Wrong address", email.getHeaderValue("To"), "test@test.edu");
    }
}
