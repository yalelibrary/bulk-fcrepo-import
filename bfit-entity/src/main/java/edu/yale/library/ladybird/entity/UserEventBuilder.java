package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class UserEventBuilder {

    private String userId;
    private String eventType;
    private Date createdDate;
    private Date startDate;
    private Date endDate;
    private String value;

    public UserEventBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public UserEventBuilder setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public UserEventBuilder setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public UserEventBuilder setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public UserEventBuilder setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public UserEventBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public UserEvent createUserEvent() {
        return new UserEvent(userId, eventType, createdDate, startDate, endDate, value);
    }
}