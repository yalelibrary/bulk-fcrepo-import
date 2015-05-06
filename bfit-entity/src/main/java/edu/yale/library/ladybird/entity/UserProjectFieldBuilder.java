package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class UserProjectFieldBuilder {
    private Date date;
    private int userId;
    private int projectId;
    private int fdid;
    private String role;

    public UserProjectFieldBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public UserProjectFieldBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public UserProjectFieldBuilder setProjectId(int projectId) {
        this.projectId = projectId;
        return this;
    }

    public UserProjectFieldBuilder setFdid(int fdid) {
        this.fdid = fdid;
        return this;
    }

    public UserProjectFieldBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public UserProjectField createUserProjectField() {
        return new UserProjectField(date, userId, projectId, fdid, role);
    }
}