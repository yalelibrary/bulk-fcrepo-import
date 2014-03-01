package edu.yale.library.beans;


import java.util.Date;

/**
 * HydraPublish
 */
public class HydraPublish implements java.io.Serializable {


    private Integer hydraPublishId;
    private Date date;
    private Integer hydraContentmodelId;
    private Integer collectionId;
    private Integer projectId;
    private Integer oid;
    private Integer oid_1;
    private Integer zindex;
    private Integer boid;
    private Integer bindex;
    private String hydraid;
    private String action;
    private Date dateready;
    private Date datehydrastart;
    private Date datehydraend;
    private Date dateaudit;
    private Integer attempts;
    private Integer priority;
    private String server;

    public HydraPublish() {
    }

    public Integer getHydraPublishId() {
        return this.hydraPublishId;
    }

    public void setHydraPublishId(Integer hydraPublishId) {
        this.hydraPublishId = hydraPublishId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getHydraContentmodelId() {
        return this.hydraContentmodelId;
    }

    public void setHydraContentmodelId(Integer hydraContentmodelId) {
        this.hydraContentmodelId = hydraContentmodelId;
    }

    public Integer getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getOid() {
        return this.oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public Integer getOid_1() {
        return this.oid_1;
    }

    public void setOid_1(Integer oid_1) {
        this.oid_1 = oid_1;
    }

    public Integer getZindex() {
        return this.zindex;
    }

    public void setZindex(Integer zindex) {
        this.zindex = zindex;
    }

    public Integer getBoid() {
        return this.boid;
    }

    public void setBoid(Integer boid) {
        this.boid = boid;
    }

    public Integer getBindex() {
        return this.bindex;
    }

    public void setBindex(Integer bindex) {
        this.bindex = bindex;
    }

    public String getHydraid() {
        return this.hydraid;
    }

    public void setHydraid(String hydraid) {
        this.hydraid = hydraid;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDateready() {
        return this.dateready;
    }

    public void setDateready(Date dateready) {
        this.dateready = dateready;
    }

    public Date getDatehydrastart() {
        return this.datehydrastart;
    }

    public void setDatehydrastart(Date datehydrastart) {
        this.datehydrastart = datehydrastart;
    }

    public Date getDatehydraend() {
        return this.datehydraend;
    }

    public void setDatehydraend(Date datehydraend) {
        this.datehydraend = datehydraend;
    }

    public Date getDateaudit() {
        return this.dateaudit;
    }

    public void setDateaudit(Date dateaudit) {
        this.dateaudit = dateaudit;
    }

    public Integer getAttempts() {
        return this.attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getServer() {
        return this.server;
    }

    public void setServer(String server) {
        this.server = server;
    }


}


