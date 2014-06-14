package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * Object
 */
public class Object implements java.io.Serializable {


    private Integer oid;
    private int projectId;
    private Date date;
    private int roid;

    public Object() {
    }

    public Object(int projectId) {
        this.projectId = projectId;
    }

    public Object(int projectId, Date date) {
        this.projectId = projectId;
        this.date = date;
    }

    public Integer getOid() {
        return this.oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRoid() {
        return roid;
    }

    public void setRoid(final int roid) {
        this.roid = roid;
    }

    @Override
    public String toString() {
        return "Object{"
                + "oid=" + oid
                + ", projectId=" + projectId
                + ", date=" + date
                + ", roid=" + roid
                + '}';
    }
}


