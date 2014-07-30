package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ObjectVersionBuilder {
    private Integer oid;
    private Integer versionId;
    private Date creationDate;
    private Integer userId;
    private String notes;

    public ObjectVersionBuilder setOid(Integer oid) {
        this.oid = oid;
        return this;
    }

    public ObjectVersionBuilder setVersionId(Integer versionId) {
        this.versionId = versionId;
        return this;
    }

    public ObjectVersionBuilder setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public ObjectVersionBuilder setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public ObjectVersionBuilder setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public ObjectVersion createObjectVersion() {
        return new ObjectVersion(oid, versionId, creationDate, userId, notes);
    }
}