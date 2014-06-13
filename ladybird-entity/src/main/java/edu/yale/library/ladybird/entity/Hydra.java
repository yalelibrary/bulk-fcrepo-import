package edu.yale.library.ladybird.entity;

import java.util.Date;

public class Hydra implements java.io.Serializable {

    private Integer id;
    private Date date;
    private int oid;
    private Date datePublish;

    public Hydra() {
    }


    public Hydra(Date date, int oid) {
        this.date = date;
        this.oid = oid;
    }

    public Hydra(Date date, int oid, Date datePublish) {
        this.date = date;
        this.oid = oid;
        this.datePublish = datePublish;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getOid() {
        return this.oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public Date getDatePublish() {
        return this.datePublish;
    }

    public void setDatePublish(Date datePublish) {
        this.datePublish = datePublish;
    }

}


