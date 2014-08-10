package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ObjectAcidBuilder {
    private Date date;
    private int userId;
    private int objectId;
    private int value;
    private int fdid;
    private ObjectAcid o;

    public ObjectAcidBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ObjectAcidBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public ObjectAcidBuilder setObjectId(int objectId) {
        this.objectId = objectId;
        return this;
    }

    public ObjectAcidBuilder setValue(int value) {
        this.value = value;
        return this;
    }

    public ObjectAcidBuilder setFdid(int fdid) {
        this.fdid = fdid;
        return this;
    }

    public ObjectAcidBuilder setO(ObjectAcid o) {
        this.o = o;
        return this;
    }

    public ObjectAcid createObjectAcid() {
        return new ObjectAcid(date, userId, objectId, value, fdid);
    }

    public ObjectAcid createObjectAcid2() {
        return new ObjectAcid(o);
    }
}