package edu.yale.library.ladybird.kernel.events;


import com.dumbster.smtp.SimpleSmtpServer;
import edu.yale.library.ladybird.kernel.ApplicationBootstrap;
import edu.yale.library.ladybird.entity.UserBuilder;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class NotificationSchedulerTest {

    /**
     * Tests notification scheduling and notification job firing
     * @throws Exception
     */
    @Test
    public void testAltNotificationScheduler() throws Exception {

        SimpleSmtpServer server = null;
        try {
            server = SimpleSmtpServer.start(8082); //FIXME
            ApplicationBootstrap kernelContext = new ApplicationBootstrap();
            kernelContext.setAbstractModule(new TestJobModule());
            ApplicationBootstrap.initNotificationScheduler();
            //Add dummy event:
            NotificationEventQueue.addEvent(new NotificationEventQueue().
                    new NotificationItem(new ImportEvent(), Collections.singletonList(new UserBuilder().createUser()), "message", "sujet"));
            Thread.sleep(6000); //TODO
        } catch (Exception e) {
            fail("Exception= " + e);
        } finally {
            try {
                if (server != null) {
                    server.stop();
                }
            } catch (Exception e) {
                fail("Error stopping server.");
            }
        }
        assertTrue(server.getReceivedEmailSize() == 1);
    }
}
