package edu.yale.library.ladybird.engine.cron;


import com.dumbster.smtp.SimpleSmtpServer;
import edu.yale.library.entity.model.Monitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ImportExportSchedulerTest {

    private SimpleSmtpServer server = null;
    public static final int PORT = 8082;


    @Before
    public void setup() {
        try {
            server = SimpleSmtpServer.start(PORT);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error starting server");
        }
    }

    @After
    public void tearDown() {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            fail("Error stopping server.");
        }
    }

    /**
     * Tests job scheduling and firing
     * @throws Exception
     */
    @Test
    public void testImportScheduler() throws Exception {
        if (server.isStopped()) {
            fail("Server stopped");
        }
        assertTrue(server.getReceivedEmailSize() == 0);
        try {
            ImportJobFactory.setInstance(new DummyImportEngineJob());
            final ImportScheduler importScheduler = new ImportScheduler();
            importScheduler.scheduleJob("test", getSimpleTrigger());
            Thread.sleep(3000); //TODO
        } catch (Exception e) {
            fail("Exception= " + e);
        }
        assertEquals("Server received wrong number of emails", server.getReceivedEmailSize(), 1);
    }

    /**
     * Tests job scheduling and firing
     * @throws Exception
     */
    @Test
    public void testExportScheduler() throws Exception {
        assertTrue(server.getReceivedEmailSize() == 0);
        try {
            ExportJobFactory.setInstance(new DummyImportEngineJob());
            final ExportScheduler exportScheduler = new ExportScheduler();
            final Monitor monitorUnit = new Monitor();
            monitorUnit.setUser(null);
            exportScheduler.scheduleJob("test", monitorUnit, getSimpleTrigger());
            Thread.sleep(3000); //TODO
        } catch (Exception e) {
            fail("Exception= " + e);
        }
        assertEquals("Server received wrong number of emails", server.getReceivedEmailSize(), 1);
    }

    private Trigger getSimpleTrigger() {
        return TriggerBuilder
                .newTrigger()
                .withIdentity("dummyTriggerName", "group1")
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(3)
                                .withRepeatCount(0))
                                .build();

    }
}
