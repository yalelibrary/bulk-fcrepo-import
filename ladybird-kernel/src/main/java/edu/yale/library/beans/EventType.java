package edu.yale.library.beans;


import java.util.Date;

/**
 * EventType
 */
public class EventType implements java.io.Serializable {


    private Integer eventTypeId;
    private Date date;
    private String label;
    private String category;

    public EventType() {
    }

    public EventType(Date date, String label, String category) {
        this.date = date;
        this.label = label;
        this.category = category;
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

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}


