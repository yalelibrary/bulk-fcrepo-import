package edu.yale.library.entity.model;

import java.util.Date;

public class UserBuilder {
    private Date date;
    private String username;
    private String password;
    private Date dateCreated;
    private int creatorId;
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

    public UserBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public UserBuilder setCreatorId(int creatorId) {
        this.creatorId = creatorId;
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
        return new User(date, username, password, dateCreated, creatorId, name, email);
    }
}