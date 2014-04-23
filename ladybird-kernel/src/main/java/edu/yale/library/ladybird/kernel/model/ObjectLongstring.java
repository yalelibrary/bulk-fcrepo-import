package edu.yale.library.ladybird.kernel.model;


import java.util.Date;

/**
 * ObjectLongstring
 */
public class ObjectLongstring implements java.io.Serializable {


    private Integer dataId;
    private Date date;
    private int userId;
    private String oid;
    private byte[] value;
    private int fdid;

    public ObjectLongstring() {
    }

    public ObjectLongstring(Date date, int userId, String oid, byte[] value, int fdid) {
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

    public String getOid() {
        return this.oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public int getFdid() {
        return this.fdid;
    }

    public void setFdid(int fdid) {
        this.fdid = fdid;
    }


}


