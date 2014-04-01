package edu.yale.library.ladybird.tests;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import edu.yale.library.ladybird.kernel.beans.User;
import edu.yale.library.ladybird.kernel.beans.UserBuilder;
import edu.yale.library.ladybird.kernel.events.EMailNotificationHandler;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class EMailNotificationHandlerIT {

    private static final int TEST_PORT = 8099; //TODO

    @Test
    public void testSend() {
        final SimpleSmtpServer server = SimpleSmtpServer.start(TEST_PORT);
        final ImportEvent testEvent = new ImportEvent();

        try {
            final EMailNotificationHandler notificationHandler = new EMailNotificationHandler();
            final User user = new UserBuilder().createUser();
            user.setEmail("test@test.edu");
            notificationHandler.notifyUser(user, testEvent);
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
        assertEquals("Wrong subject", email.getHeaderValue("Subject"), testEvent.getEventName());
        assertEquals("Wrong address", email.getHeaderValue("To"), "\"test@test.edu\" <test@test.edu>");
    }
}
