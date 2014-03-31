package edu.yale.library.ladybird.kernel.beans;

import java.util.Date;

public class UserBuilder {
    private Date date;
    private String username;
    private Date dateCreated;
    private Date dateEdited;
    private Date dateLastused;
    private int userId_1;
    private String password;
    private String name;
    private String email;

    public UserBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public UserBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public UserBuilder setDateEdited(Date dateEdited) {
        this.dateEdited = dateEdited;
        return this;
    }

    public UserBuilder setDateLastused(Date dateLastused) {
        this.dateLastused = dateLastused;
        return this;
    }

    public UserBuilder setUserId_1(int userId_1) {
        this.userId_1 = userId_1;
        return this;
    }

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public User createUser() {
        return new User(date, username, dateCreated, dateEdited, dateLastused, userId_1);
    }
}