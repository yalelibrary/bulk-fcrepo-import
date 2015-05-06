package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * User
 *
 * @author Osman Din
 */
public class User implements java.io.Serializable {

    private Integer userId;
    private Date date;
    private String username;
    private String password;
    private Date dateCreated;
    private Date dateEdited;
    private Date dateLastused;
    private int creatorId;
    private String name;
    private String email;
    private String role; //TODO

    public User() {
    }


    public User(Date date, String username, String password, Date dateCreated, int creatorId, String name, String email) {
        this.date = date;
        this.username = username;
        this.password = password;
        this.dateCreated = dateCreated;
        this.creatorId = creatorId;
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

    public int getCreatorId() {
        return this.creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{"
                + "userId=" + userId
                //+ ", date=" + date
                + ", username='" + username + '\''
                //+ ", creatorId=" + creatorId
                //+ ", name='" + name + '\''
                + ", email='" + email + '\''
                + '}';
    }
}


