package edu.yale.library.ladybird.kernel.events.imports;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.events.UserGeneratedEvent;

import java.util.Date;

/**
 * An import event. Classes may subcass this. Subject to modification.
 */
public class ImportEvent implements UserGeneratedEvent {
    /* User requesting action */
    private User user;

    /* TIme started */
    private Date startTime;

    /* Time ended */
    private Date endTime;

    private final String eventName = "Ladybird ImportEvent";

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String getPrincipal() {
        return user.getUsername();
    }

    @Override
    public String getValue() {
        return null;
    }
}
