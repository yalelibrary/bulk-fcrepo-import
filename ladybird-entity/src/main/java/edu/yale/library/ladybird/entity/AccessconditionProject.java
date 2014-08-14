package edu.yale.library.ladybird.entity;

import java.util.Date;

public class AccessconditionProject implements java.io.Serializable {


    private Integer id;
    private Date date;
    private int userId;
    private int projectId;
    private int accessconditionTypeId;
    private int accessconditionTargetId;
    private String value;

    public AccessconditionProject() {
    }


    public AccessconditionProject(Date date, int userId, int projectId, int accessconditionTypeId, int accessconditionTargetId) {
        this.date = date;
        this.userId = userId;
        this.projectId = projectId;
        this.accessconditionTypeId = accessconditionTypeId;
        this.accessconditionTargetId = accessconditionTargetId;
    }

    public AccessconditionProject(Date date, int userId, int projectId, int accessconditionTypeId, int accessconditionTargetId, String value) {
        this.date = date;
        this.userId = userId;
        this.projectId = projectId;
        this.accessconditionTypeId = accessconditionTypeId;
        this.accessconditionTargetId = accessconditionTargetId;
        this.value = value;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    @Override
    public String toString() {
        return "AccessconditionProject{"
                + "id=" + id
                + ", date=" + date
                + ", userId=" + userId
                + ", projectId=" + projectId
                + ", accessconditionTypeId=" + accessconditionTypeId
                + ", accessconditionTargetId=" + accessconditionTargetId
                + ", value='" + value + '\''
                + '}';
    }
}


