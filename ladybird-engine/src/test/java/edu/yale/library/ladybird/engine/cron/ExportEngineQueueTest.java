package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.engine.TestModule;
import edu.yale.library.ladybird.engine.exports.ExportRequestEvent;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import org.junit.Test;

public class ExportEngineQueueTest {

    @Test
    public void shouldPostEvent() {
        KernelBootstrap kernelBootstrap = new KernelBootstrap();
        kernelBootstrap.setAbstractModule(new TestModule());
        ExportRequestEvent exportRequestEvent = new ExportRequestEvent(555);
        ExportEngineQueue.addJob(exportRequestEvent);
        assert (DummyEventCounter.getEventCount() == 1);
    }
}
