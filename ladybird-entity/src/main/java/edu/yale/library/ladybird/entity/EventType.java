package edu.yale.library.ladybird.entity;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple class meant to be exteneded. EventType basically represents the schema event_type table.
 * Children are mapped to this single table using a discriminator column (label). Children type should just construct
 * the supertype with the approrpriate label. Children in other words are meant to be dumb objects.
 *
 * ObjectEvents have a many-to-one relationship to an EventType. An ObjectEvent has a reference to an EventType
 * since it has a FK in the schema. Event types must be registered (i.e. written to db) at init.
 *
 * The design allows an EventType to have rich metadata or columns. If only the label column is used, it might
 * make more sense just to have a single type ObjectEvent (with column 'label') and delete EventType.
 *
 * Another approach could be using an enum within ObjectEvent to represent an EventType -- but this would prevent
 * rich metadata possibilities.
 *
 * An EventType must be added in: (1) AppContextListerner (2) hibernate configuration
 *
 * @see edu.yale.library.ladybird.entity.event.EventLabel
 * @see edu.yale.library.ladybird.entity.event.UserEditEvent for an example EventType
*/

public class EventType implements java.io.Serializable {

    /** Currently, auto-incremented id. */
    private Integer eventTypeId;

    @Deprecated
    private Date date; //TODO remove

    /** name of the event */
    private String label;

    @Deprecated
    private String category; //Not used. Original intent was to use it for 'premis' events.

    private Set<ObjectEvent> objectEvents = new HashSet<>(0);

    public EventType() {
    }

    public EventType(String label) {
        this.label = label;
    }

    public EventType(Date date, String label, String category, Set<ObjectEvent> objectEvents) {
        this.date = date;
        this.label = label;
        this.category = category;
        this.objectEvents = objectEvents;
    }

    public Integer getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Deprecated
    public String getCategory() {
        return this.category;
    }

    @Deprecated
    public void setCategory(String category) {
        this.category = category;
    }
    public Set<ObjectEvent> getObjectEvents() {
        return this.objectEvents;
    }

    public void setObjectEvents(Set<ObjectEvent> objectEvents) {
        this.objectEvents = objectEvents;
    }

    @Override
    public String toString() {
        return "EventType{"
                + "eventTypeId=" + eventTypeId
                + ", date=" + date
                + ", label='" + label + '\''
                + '}';
    }
}


