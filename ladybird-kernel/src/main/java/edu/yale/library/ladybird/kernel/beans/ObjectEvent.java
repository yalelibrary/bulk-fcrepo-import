package edu.yale.library.ladybird.kernel.beans;


import java.util.Date;

/**
 * ObjectEvent
 */
public class ObjectEvent implements java.io.Serializable {


    private Integer eventId;
    private int eventTypeId;
    private int userId;
    private Date date;
    private int oid;
    private String event;

    public ObjectEvent() {
    }

    public ObjectEvent(int eventTypeId, int userId, Date date, int oid, String event) {
        this.eventTypeId = eventTypeId;
        this.userId = userId;
        this.date = date;
        this.oid = oid;
        this.event = event;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
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

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


}


