package edu.yale.library.ladybird.kernel.events.exports;

import edu.yale.library.ladybird.kernel.events.Event;
import edu.yale.library.ladybird.kernel.events.UserGeneratedEvent;

import java.util.Date;

/**
 * An import event. Classes may subcass this. Subject to modification.
 */
public class ExportEvent implements UserGeneratedEvent {
    /* TIme started */
    private Date startTime;

    /* Time ended */
    private Date endTime;

    private final String eventName = "Ladybird ExportEvent";

    @Override
    public String getEventName() {
       return eventName;
    }

    @Override
    public String getPrincipal() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }
}
