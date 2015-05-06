package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportJobNotifications implements java.io.Serializable {

    private int id;
    private Integer importJobId;
    private int userId;
    private Byte notified = 0;
    private Date dateCreated;
    private Integer numTries = 0;
    private Date dateTried;

    public ImportJobNotifications() {
    }


    public ImportJobNotifications(int id) {
        this.id = id;
    }

    public ImportJobNotifications(int id, Integer importJobId, int userId, Byte notified, Date dateCreated, Integer numTries, Date dateTried) {
        this.id = id;
        this.importJobId = importJobId;
        this.userId = userId;
        this.notified = notified;
        this.dateCreated = dateCreated;
        this.numTries = numTries;
        this.dateTried = dateTried;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getImportJobId() {
        return this.importJobId;
    }

    public void setImportJobId(Integer importJobId) {
        this.importJobId = importJobId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Byte getNotified() {
        return this.notified;
    }

    public void setNotified(Byte notified) {
        this.notified = notified;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getNumTries() {
        return this.numTries;
    }

    public void setNumTries(Integer numTries) {
        this.numTries = numTries;
    }

    public Date getDateTried() {
        return this.dateTried;
    }

    public void setDateTried(Date dateTried) {
        this.dateTried = dateTried;
    }

    @Override
    public String toString() {
        return "ImportJobNotifications{"
                + "id=" + id
                + ", importJobId=" + importJobId
                + ", userId=" + userId
                + ", notified=" + notified
                + ", dateCreated=" + dateCreated
                + ", numTries=" + numTries
                + ", dateTried=" + dateTried
                + '}';
    }
}


