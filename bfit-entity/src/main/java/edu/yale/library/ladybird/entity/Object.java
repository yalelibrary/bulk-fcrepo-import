package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class Object implements java.io.Serializable {

    private Integer oid;
    private int projectId;
    private Date date;
    private int roid;
    private boolean parent;
    private Integer p_oid;
    private Integer zindex;
    private int userId;

    public Object() {
    }


    public Object(Integer oid, int projectId, Date date, int roid, boolean parent, Integer p_oid, Integer zindex, int userId) {
        this.oid = oid;
        this.projectId = projectId;
        this.date = date;
        this.roid = roid;
        this.parent = parent;
        this.p_oid = p_oid;
        this.zindex = zindex;
        this.userId = userId;
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

    public boolean isParent() {
        return parent;
    }

    public boolean isChild() {
        return !parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public Integer getP_oid() {
        return p_oid;
    }

    public void setP_oid(Integer p_oid) {
        this.p_oid = p_oid;
    }

    public Integer getZindex() {
        return zindex;
    }

    public void setZindex(Integer zindex) {
        this.zindex = zindex;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "Object{"
                + "oid=" + oid
                + ", projectId=" + projectId
                + ", date=" + date
                + ", roid=" + roid
                + ", parent=" + parent
                + ", p_oid=" + p_oid
                + ", zindex=" + zindex
                + ", userId=" + userId
                + '}';
    }
}


