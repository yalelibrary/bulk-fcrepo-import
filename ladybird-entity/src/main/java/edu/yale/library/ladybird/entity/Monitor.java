package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * Represents input-output folder pair and user.
 */
public class Monitor implements java.io.Serializable {

    private Integer id;
    private String dirPath;
    private String exportPath;
    private User user = new UserBuilder().createUser();
    private Date date = new Date();

    @Deprecated
    private String notificationEmail; //todo remove

    public Monitor() {

    }

    public Monitor(Integer id, String dirPath) {
        this.id = id;
        this.dirPath = dirPath;
    }

    public Monitor(Integer id, String dirPath, String exportPath) {
        this.id = id;
        this.dirPath = dirPath;
        this.exportPath = exportPath;
    }

    public Monitor(Integer id, String dirPath, String exportPath, User user) {
        this.id = id;
        this.dirPath = dirPath;
        this.exportPath = exportPath;
        this.user = user;
    }

    public Monitor(Integer id, String dirPath, String exportPath, User user, String notificationEmail) {
        this.id = id;
        this.dirPath = dirPath;
        this.exportPath = exportPath;
        this.user = user;
        this.notificationEmail = notificationEmail;
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

    @Override
    public String toString() {
        return "Monitor{"
                + "id=" + id
                + ", dirPath='" + dirPath + '\''
                + ", exportPath='" + exportPath + '\''
                + ", user=" + user
                + ", notificationEmail='" + notificationEmail + '\''
                + '}';
    }
}


