package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ObjectVersion implements java.io.Serializable {

    private Integer id;
    private Integer oid;
    private Integer versionId;
    private Date creationDate;
    private Integer userId;
    private String notes;

    public ObjectVersion() {
    }

    public ObjectVersion(Integer oid, Integer versionId, Date creationDate, Integer userId, String notes) {
        this.oid = oid;
        this.versionId = versionId;
        this.creationDate = creationDate;
        this.userId = userId;
        this.notes = notes;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOid() {
        return this.oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public Integer getVersionId() {
        return this.versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ObjectVersion{"
                + "id=" + id
                + ", oid=" + oid
                + ", versionId=" + versionId
                + ", creationDate=" + creationDate
                + ", userId=" + userId
                +                ", notes='" + notes + '\'' +      '}';
    }
}


