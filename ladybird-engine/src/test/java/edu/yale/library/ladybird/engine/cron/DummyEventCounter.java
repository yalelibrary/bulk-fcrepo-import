package edu.yale.library.ladybird.engine.cron;

import com.google.common.eventbus.Subscribe;
import edu.yale.library.ladybird.kernel.events.exports.ExportEvent;

public class DummyEventCounter {

    private static int count = 0;

    @Subscribe
    public void recordEventChange(ExportEvent e) {
        count++;
    }

    public static int getEventCount() {
        return count;
    }
}
