package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ObjectEvent implements java.io.Serializable {


    private Integer eventId;
    private EventType eventType;
    private int userId;
    private Date date;
    private int oid;

    public ObjectEvent() {
    }

    public ObjectEvent(EventType eventType, int userId, Date date, int oid, String event) {
        this.eventType = eventType;
        this.userId = userId;
        this.date = date;
        this.oid = oid;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
    public EventType getEventType() {
        return this.eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public int getOid() {
        return this.oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    @Override
    public String toString() {
        return "ObjectEvent{"
                + "eventId=" + eventId + ", eventType=" + eventType + ", userId=" + userId + ", date=" + date
                + ", oid=" + oid + '}';
    }
}


