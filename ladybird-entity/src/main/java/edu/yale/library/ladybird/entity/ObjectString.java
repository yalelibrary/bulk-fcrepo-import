package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * ObjectString
 */
public class ObjectString implements java.io.Serializable {


    private Integer dataId;
    private Date date;
    private int userId;
    private int oid;
    private String value;
    private int fdid;

    public ObjectString() {
    }

    public ObjectString(Date date, int userId, int oid, String value, int fdid) {
        this.date = date;
        this.userId = userId;
        this.oid = oid;
        this.value = value;
        this.fdid = fdid;
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

    @Override
    public String toString() {
        return "ObjectString{"
                + "dataId=" + dataId
                + ", date=" + date
                + ", userId=" + userId
                + ", oid=" + oid
                + ", value='" + value + '\''
                + ", fdid=" + fdid
                + '}';
    }
}


