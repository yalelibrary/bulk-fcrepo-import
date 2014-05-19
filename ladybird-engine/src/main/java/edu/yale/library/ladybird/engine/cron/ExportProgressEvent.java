package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.kernel.events.Event;
import edu.yale.library.ladybird.kernel.events.exports.ExportEvent;

/**
 *
 */
public class ExportProgressEvent extends ExportEvent {

    private Event completedEvent;

    private Integer jobId; //or choose the above field for right abstraction

    public ExportProgressEvent(Event completedEvent, Integer jobId) {
        this.completedEvent = completedEvent;
        this.jobId = jobId;
    }

    public Event getCompletedEvent() {
        return completedEvent;
    }

    public Integer getJobId() {
        return jobId;
    }

    @Override
    public String toString() {
        return "ExportProgressEvent{"
                + "completedEvent=" + completedEvent
                + ", jobId=" + jobId
                + '}';
    }
}
