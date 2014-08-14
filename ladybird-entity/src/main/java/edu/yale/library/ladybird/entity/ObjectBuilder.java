package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ObjectBuilder {
    private Integer oid;
    private int projectId;
    private Date date;
    private int roid;
    private boolean parent;
    private Integer p_oid;
    private Integer zindex;
    private int userId;

    public ObjectBuilder setOid(Integer oid) {
        this.oid = oid;
        return this;
    }

    public ObjectBuilder setProjectId(int projectId) {
        this.projectId = projectId;
        return this;
    }

    public ObjectBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ObjectBuilder setRoid(int roid) {
        this.roid = roid;
        return this;
    }

    public ObjectBuilder setParent(boolean parent) {
        this.parent = parent;
        return this;
    }

    public ObjectBuilder setP_oid(Integer p_oid) {
        this.p_oid = p_oid;
        return this;
    }

    public ObjectBuilder setZindex(Integer zindex) {
        this.zindex = zindex;
        return this;
    }

    public ObjectBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Object createObject() {
        return new Object(oid, projectId, date, roid, parent, p_oid, zindex, userId);
    }

}