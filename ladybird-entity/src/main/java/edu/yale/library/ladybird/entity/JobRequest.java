package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * Represents input-output folder pair and user.
 */
public class JobRequest implements java.io.Serializable {

    private Integer id;
    private String dirPath;
    private String exportPath;
    private User user = new UserBuilder().createUser();
    private Date date = new Date();
    private Project currentProject = new Project();
    private Integer currentUserId;
    private Integer currentProjectId;

    @Deprecated
    private String notificationEmail; //todo remove

    public JobRequest() {

    }

    public JobRequest(Integer id, String dirPath) {
        this.id = id;
        this.dirPath = dirPath;
    }

    public JobRequest(Integer id, String dirPath, String exportPath) {
        this.id = id;
        this.dirPath = dirPath;
        this.exportPath = exportPath;
    }

    public JobRequest(Integer id, String dirPath, String exportPath, User user) {
        this.id = id;
        this.dirPath = dirPath;
        this.exportPath = exportPath;
        this.user = user;
    }

    public JobRequest(Integer id, String dirPath, String exportPath, User user, String notificationEmail) {
        this.id = id;
        this.dirPath = dirPath;
        this.exportPath = exportPath;
        this.user = user;
        this.notificationEmail = notificationEmail;
    }

    public JobRequest(Integer id, String dirPath, String exportPath, User user, Date date, Project currentProject) {
        this.id = id;
        this.dirPath = dirPath;
        this.exportPath = exportPath;
        this.user = user;
        this.date = date;
        this.currentProject = currentProject;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExportPath() {
        return exportPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public Integer getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Integer currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Integer getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProjectId(Integer currentProjectId) {
        this.currentProjectId = currentProjectId;
    }

    @Override
    public String toString() {
        return "JobRequest{"
                + "id=" + id
                //+ ", dirPath='"
                //+ dirPath + '\''
                + ", exportPath='" + exportPath + '\''
                + ", user=" + user.getUsername()
                + ", date=" + date
                + ", currentProject=" + currentProject
                + ", notificationEmail='" + notificationEmail + '\''
                + ", currentProjectId=" + currentProjectId
                + ", currentUserId=" + currentUserId
                + '}';
    }
}


