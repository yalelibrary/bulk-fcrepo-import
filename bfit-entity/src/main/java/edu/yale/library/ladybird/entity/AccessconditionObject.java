package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class AccessconditionObject implements java.io.Serializable {

    private int id;
    private Date date;
    private int oid;
    private int accessconditionTypeId;
    private int accessconditionTargetId;
    private String value;

    public AccessconditionObject() {
    }


    public AccessconditionObject(int id, Date date, int oid, int accessconditionTypeId, int accessconditionTargetId) {
        this.id = id;
        this.date = date;
        this.oid = oid;
        this.accessconditionTypeId = accessconditionTypeId;
        this.accessconditionTargetId = accessconditionTargetId;
    }

    public AccessconditionObject(int id, Date date, int oid, int accessconditionTypeId, int accessconditionTargetId, String value) {
        this.id = id;
        this.date = date;
        this.oid = oid;
        this.accessconditionTypeId = accessconditionTypeId;
        this.accessconditionTargetId = accessconditionTargetId;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getOid() {
        return this.oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getAccessconditionTypeId() {
        return this.accessconditionTypeId;
    }

    public void setAccessconditionTypeId(int accessconditionTypeId) {
        this.accessconditionTypeId = accessconditionTypeId;
    }

    public int getAccessconditionTargetId() {
        return this.accessconditionTargetId;
    }

    public void setAccessconditionTargetId(int accessconditionTargetId) {
        this.accessconditionTargetId = accessconditionTargetId;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public boolean equals(java.lang.Object other) {
        if ((this == other)) {
            return true;
        }
        if ((other == null)) {
            return false;
        }

        if (!(other instanceof AccessconditionObject)) {
            return false;
        }
        AccessconditionObject castOther = (AccessconditionObject) other;

        return (this.getId() == castOther.getId())
                && ((this.getDate() == castOther.getDate()) || (this.getDate() != null && castOther.getDate() != null && this.getDate().equals(castOther.getDate())))
                && (this.getOid() == castOther.getOid())
                && (this.getAccessconditionTypeId() == castOther.getAccessconditionTypeId())
                && (this.getAccessconditionTargetId() == castOther.getAccessconditionTargetId())
                && ((this.getValue() == castOther.getValue()) || (this.getValue() != null && castOther.getValue() != null && this.getValue().equals(castOther.getValue())));
    }

    public int hashCode() {
        int result = 17;

        result = 37 * result + this.getId();
        result = 37 * result + (getDate() == null ? 0 : this.getDate().hashCode());
        result = 37 * result + this.getOid();
        result = 37 * result + this.getAccessconditionTypeId();
        result = 37 * result + this.getAccessconditionTargetId();
        result = 37 * result + (getValue() == null ? 0 : this.getValue().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AccessconditionObject{"
                + "id=" + id
                + ", date=" + date
                + ", oid=" + oid
                + ", accessconditionTypeId=" + accessconditionTypeId
                + ", accessconditionTargetId=" + accessconditionTargetId
                + ", value='" + value + '\''
                + '}';
    }
}


