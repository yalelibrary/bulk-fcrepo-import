package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ObjectEventBuilder {
    private EventType eventType;
    private int userId;
    private Date date;
    private int oid;
    private String event;

    public ObjectEventBuilder setEventType(final EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public ObjectEventBuilder setUserId(final int userId) {
        this.userId = userId;
        return this;
    }

    public ObjectEventBuilder setDate(final Date date) {
        this.date = date;
        return this;
    }

    public ObjectEventBuilder setOid(final int oid) {
        this.oid = oid;
        return this;
    }

    @Deprecated
    public ObjectEventBuilder setEvent(final String event) {
        this.event = event;
        return this;
    }

    public ObjectEvent createObjectEvent() {
        return new ObjectEvent(eventType, userId, date, oid, event);
    }
}