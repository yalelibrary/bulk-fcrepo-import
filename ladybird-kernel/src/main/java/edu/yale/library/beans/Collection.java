package edu.yale.library.beans;


import java.util.Date;

/**
 * Collection
 */
public class Collection implements java.io.Serializable {


    private Integer collectionId;
    private String label;
    private Date date;

    public Collection() {
    }

    public Collection(String label, Date date) {
        this.label = label;
        this.date = date;
    }

    public Integer getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
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


}


