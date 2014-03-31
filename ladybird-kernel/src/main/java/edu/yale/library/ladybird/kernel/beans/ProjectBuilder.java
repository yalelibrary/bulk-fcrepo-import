package edu.yale.library.ladybird.kernel.beans;

import java.util.Date;

public class ProjectBuilder {
    private String label;
    private Date date;
    private Integer projectId;
    private Integer userId;
    private String location;
    private String url;
    private String add1;
    private String add2;
    private String city;
    private String state;
    private String zip;
    private String phone;

    public ProjectBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public ProjectBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ProjectBuilder setProjectId(Integer projectId) {
        this.projectId = projectId;
        return this;
    }

    public ProjectBuilder setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public ProjectBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ProjectBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public ProjectBuilder setAdd1(String add1) {
        this.add1 = add1;
        return this;
    }

    public ProjectBuilder setAdd2(String add2) {
        this.add2 = add2;
        return this;
    }

    public ProjectBuilder setCity(String city) {
        this.city = city;
        return this;
    }

    public ProjectBuilder setState(String state) {
        this.state = state;
        return this;
    }

    public ProjectBuilder setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public ProjectBuilder setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Project createProject() {
        return new Project(label, date);
    }
}