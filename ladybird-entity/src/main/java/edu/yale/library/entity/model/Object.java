package edu.yale.library.entity.model;


import java.util.Date;

/**
 * Object
 */
public class Object implements java.io.Serializable {


    private Integer oid;
    private int projectId;
    private Date date;

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

    @Override
    public String toString() {
        return "Object{"
                + "oid=" + oid
                + ", projectId=" + projectId
                + ", date=" + date
                + '}';
    }
}


