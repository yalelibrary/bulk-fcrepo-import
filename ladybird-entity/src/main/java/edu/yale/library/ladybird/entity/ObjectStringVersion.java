package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * ObjectStringVersion
 */
public class ObjectStringVersion implements java.io.Serializable {


    private Integer dataId;
    private Date date;
    //not used
    private int userId;
    private int oid;
    private String value;
    private int fdid;
    private int versionId;

    public ObjectStringVersion() {
    }

    public ObjectStringVersion(ObjectString copy) {
        this.date = copy.getDate();
        this.userId = copy.getUserId();
        this.oid = copy.getOid();
        this.value = copy.getValue();
        this.fdid = copy.getFdid();
    }

    public ObjectStringVersion(Date date, int userId, int oid, String value, int fdid, int versionId) {
        this.date = date;
        this.userId = userId;
        this.oid = oid;
        this.value = value;
        this.fdid = fdid;
        this.versionId = versionId;
    }

    public Integer getDataId() {
        return this.dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOid() {
        return this.oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFdid() {
        return this.fdid;
    }

    public void setFdid(int fdid) {
        this.fdid = fdid;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    @Override
    public String toString() {
        return "ObjectStringVersion{" + "dataId=" + dataId + ", date=" + date + ", userId=" + userId + ", oid=" + oid
                + ", value='" + value + '\'' + ", fdid=" + fdid + ", versionId=" + versionId + '}';
    }
}


