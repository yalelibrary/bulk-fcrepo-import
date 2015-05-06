package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class UserProjectField implements java.io.Serializable {
    private Integer id;
    private Date date;
    private int userId;
    private int projectId;
    private int fdid;
    private String role;

    public UserProjectField() {
    }

    public UserProjectField(Date date, int userId, int projectId, int fdid, String role) {
        this.date = date;
        this.userId = userId;
        this.projectId = projectId;
        this.fdid = fdid;
        this.role = role;
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

    public int getFdid() {
        return this.fdid;
    }

    public void setFdid(int fdid) {
        this.fdid = fdid;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserProjectField{"
                + "id=" + id
                + ", date=" + date
                + ", userId=" + userId
                + ", projectId=" + projectId
                + ", fdid=" + fdid
                + ", role='" + role + '\''
                + '}';
    }
}


