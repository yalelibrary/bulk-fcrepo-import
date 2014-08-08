package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ObjectStringBuilder {
    private ObjectString copy;
    private Date date;
    private int userId;
    private int oid;
    private String value;
    private int fdid;

    public ObjectStringBuilder setCopy(ObjectString copy) {
        this.copy = copy;
        return this;
    }

    public ObjectStringBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ObjectStringBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public ObjectStringBuilder setOid(int oid) {
        this.oid = oid;
        return this;
    }

    public ObjectStringBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public ObjectStringBuilder setFdid(int fdid) {
        this.fdid = fdid;
        return this;
    }

    public ObjectString createObjectString() {
        return new ObjectString(date, userId, oid, value, fdid);
    }
}