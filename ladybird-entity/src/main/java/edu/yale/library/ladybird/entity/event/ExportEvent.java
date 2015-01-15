package edu.yale.library.ladybird.entity.event;

/**
 * An import event. Classes may subcass this. Subject to modification.
 */
public class ExportEvent implements UserGeneratedEvent {

    private final String eventName = "Export";

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
