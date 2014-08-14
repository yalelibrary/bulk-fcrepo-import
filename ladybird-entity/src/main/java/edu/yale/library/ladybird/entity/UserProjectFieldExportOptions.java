package edu.yale.library.ladybird.entity;

public class UserProjectFieldExportOptions implements java.io.Serializable {

    private Integer id;
    private Integer userId;
    private Integer fdid;
    private Character export;
    private Integer projectId;

    public UserProjectFieldExportOptions() {
    }

    public UserProjectFieldExportOptions(Integer userId, Integer fdid, Character export, Integer projectId) {
        this.userId = userId;
        this.fdid = fdid;
        this.export = export;
        this.projectId = projectId;
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

    public Character getExport() {
        return this.export;
    }

    public void setExport(Character export) {
        this.export = export;
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

}


