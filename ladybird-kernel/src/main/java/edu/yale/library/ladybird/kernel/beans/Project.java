package edu.yale.library.ladybird.kernel.beans;


import java.util.Date;

/**
 * Project
 */
public class Project implements java.io.Serializable {


    private Integer projectId;
    private String label;
    private Date date;
    private Integer userId;
    private String location;
    private String url;
    private String add1;
    private String add2;
    private String city;
    private String state;
    private String zip;
    private String phone;

    public Project() {
    }

    public Project(String label, Date date) {
        this.label = label;
        this.date = date;
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAdd1() {
        return this.add1;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public String getAdd2() {
        return this.add2;
    }

    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Project{"
                + "projectId=" + projectId
                + ", label='" + label + '\''
                + ", date=" + date
                + ", userId=" + userId
                + ", location='" + location + '\''
                + ", url='" + url + '\''
                + ", add1='" + add1 + '\''
                + ", add2='" + add2 + '\''
                + ", city='" + city + '\''
                + ", state='" + state + '\''
                + ", zip='" + zip + '\''
                + ", phone='" + phone + '\''
                + '}';
    }

}


