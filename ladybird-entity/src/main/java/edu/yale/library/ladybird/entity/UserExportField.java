package edu.yale.library.ladybird.entity;

public class UserExportField implements java.io.Serializable {

    private Integer id;
    private Integer userId;
    private Integer fdid;

    public UserExportField() {
    }

    public UserExportField(Integer userId, Integer fdid) {
        this.userId = userId;
        this.fdid = fdid;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFdid() {
        return this.fdid;
    }

    public void setFdid(Integer fdid) {
        this.fdid = fdid;
    }

    @Override
    public String toString() {
        return "UserExportField{"
                + "userId=" + userId
                + ", fdid=" + fdid
                + '}';
    }
}


