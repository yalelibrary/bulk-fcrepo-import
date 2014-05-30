package edu.yale.library.ladybird.entity;

import java.util.Date;

public class AccessconditionTarget implements java.io.Serializable {


    private Integer accessconditionTargetId;
    private Date date;
    private int userId;
    private String label;

    public AccessconditionTarget() {
    }

    public AccessconditionTarget(Date date, int userId, String label) {
        this.date = date;
        this.userId = userId;
        this.label = label;
    }

    public Integer getAccessconditionTargetId() {
        return this.accessconditionTargetId;
    }

    public void setAccessconditionTargetId(Integer accessconditionTargetId) {
        this.accessconditionTargetId = accessconditionTargetId;
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

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "AccessconditionTarget{" + "accessconditionTargetId=" + accessconditionTargetId + ", date=" + date
                + ", userId=" + userId + ", label='" + label + '\'' + '}';
    }
}


