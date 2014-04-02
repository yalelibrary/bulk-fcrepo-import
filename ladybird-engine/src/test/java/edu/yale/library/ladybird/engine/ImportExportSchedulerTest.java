package edu.yale.library.ladybird.engine;


import com.dumbster.smtp.SimpleSmtpServer;
import edu.yale.library.ladybird.engine.cron.ExportJobFactory;
import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.engine.cron.ImportJobFactory;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.kernel.beans.Monitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class ImportExportSchedulerTest {

    private SimpleSmtpServer server = null;

    @Before
    public void setup() {
        server = SimpleSmtpServer.start(8082); //FIXME
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
        assertTrue(server.getReceivedEmailSize() == 0);
        try {
            ImportJobFactory.setInstance(new DummyImportEngineJob());
            final ImportScheduler importScheduler = new ImportScheduler();
            importScheduler.scheduleJob("test", "test", getTestImportCronSchedule());
            Thread.sleep(3000); //TODO
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception= " + e);
        }
        assertTrue(server.getReceivedEmailSize() >= 1);
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
            exportScheduler.scheduleJob("test", "test", getTestExportCronSchedule(), monitorUnit);
            Thread.sleep(3000); //TODO
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception= " + e);
        }
        assertTrue(server.getReceivedEmailSize() >= 1);
    }

    private String getTestImportCronSchedule() {
        return "0/2 * * * * ?";
    }

    private String getTestExportCronSchedule() {
        return "0/2 * * * * ?";
    }
}
