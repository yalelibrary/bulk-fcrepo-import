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

    private static final int TEST_PORT = 8099;

    @Test
    public void testSend() {
        SimpleSmtpServer server = SimpleSmtpServer.start(TEST_PORT);

        try {
            EMailNotificationHandler eMailNotificationHandler = new EMailNotificationHandler();
            User user = new UserBuilder().createUser();
            user.setEmail("osman.din@yale.edu");
            eMailNotificationHandler.notifyUser(user, new ImportEvent());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception= " + e);
        }

        server.stop();

        assertTrue(server.getReceivedEmailSize() == 1);
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) emailIter.next();
        assertEquals("Wrong subject", email.getHeaderValue("Subject"), "LadyBird Test E-mail");
    }
}
