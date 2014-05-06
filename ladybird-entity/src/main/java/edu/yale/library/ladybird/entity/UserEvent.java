package edu.yale.library.ladybird.entity;

import java.util.Date;

public class UserEvent implements java.io.Serializable {

    private Integer eventId;
    private String userId;
    private String eventType;
    private Date createdDate;
    private Date startDate;
    private Date endDate;
    private String value;

    public UserEvent() {
    }

    public UserEvent(String userId, String eventType, Date createdDate, Date startDate, Date endDate, String value) {
        this.userId = userId;
        this.eventType = eventType;
        this.createdDate = createdDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.value = value;
    }

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UserEvent{"
                + "eventId=" + eventId
                + ", userId='" + userId + '\''
                + ", eventType='" + eventType + '\''
                + ", createdDate=" + createdDate
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + ", value='" + value + '\''
                + '}';
    }
}


