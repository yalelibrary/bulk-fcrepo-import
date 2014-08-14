package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * Indicates user's role in a project
 */
public class UserProject implements java.io.Serializable {

    private Integer id;
    private Date date;
    private int userId;
    private int projectId;
    private String role;

    public UserProject() {
    }

    public UserProject(Date date, int userId, int projectId, String role) {
        this.date = date;
        this.userId = userId;
        this.projectId = projectId;
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

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserProject{"
                + "id=" + id
                + ", date=" + date
                + ", userId=" + userId
                + ", projectId=" + projectId
                + ", role='" + role + '\''
                + '}';
    }
}


