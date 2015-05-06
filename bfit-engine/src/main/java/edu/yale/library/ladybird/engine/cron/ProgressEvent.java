package edu.yale.library.ladybird.engine.cron;

import edu.yale.library.ladybird.entity.event.Event;
import edu.yale.library.ladybird.engine.JobStatus;

/**
 * An event wrapper
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ProgressEvent implements Event {

    private Integer jobId; //or choose the above field for right abstraction

    private Event event;

    private JobStatus eventStatus;

    public ProgressEvent(Integer jobId, Event event, JobStatus eventStatus) {
        this.jobId = jobId;
        this.event = event;
        this.eventStatus = eventStatus;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public JobStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(JobStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public boolean isComplete(ProgressEvent event) {
        return event.getEventStatus() == JobStatus.DONE;
    }

    @Override
    public String getEventName() {
        return "";
    }
}
