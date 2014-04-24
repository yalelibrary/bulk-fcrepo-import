package edu.yale.library.entity.model;


import java.util.Date;

/**
 * User
 */
public class User implements java.io.Serializable {


    private Integer userId;
    private Date date;
    private String username;
    private String password;
    private Date dateCreated;
    private Date dateEdited;
    private Date dateLastused;
    private int userId_1;
    private String name;
    private String email;

    public User() {
    }


    public User(Date date, String username, String password, Date dateCreated, Date dateEdited, Date dateLastused, int userId_1, String name, String email) {
        this.date = date;
        this.username = username;
        this.password = password;
        this.dateCreated = dateCreated;
        this.dateEdited = dateEdited;
        this.dateLastused = dateLastused;
        this.userId_1 = userId_1;
        this.name = name;
        this.email = email;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateEdited() {
        return this.dateEdited;
    }

    public void setDateEdited(Date dateEdited) {
        this.dateEdited = dateEdited;
    }

    public Date getDateLastused() {
        return this.dateLastused;
    }

    public void setDateLastused(Date dateLastused) {
        this.dateLastused = dateLastused;
    }

    public int getUserId_1() {
        return this.userId_1;
    }

    public void setUserId_1(int userId_1) {
        this.userId_1 = userId_1;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{"
                + "userId=" + userId
                + ", date=" + date
                + ", username='" + username + '\''
                + ", password='" + password + '\''
                + ", dateCreated=" + dateCreated
                + ", dateEdited=" + dateEdited
                + ", dateLastused=" + dateLastused
                + ", userId_1=" + userId_1
                + ", name='" + name + '\''
                + ", email='" + email + '\''
                + '}';
    }
}


