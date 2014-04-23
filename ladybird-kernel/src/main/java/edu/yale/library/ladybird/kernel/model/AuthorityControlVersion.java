package edu.yale.library.ladybird.kernel.model;


import java.util.Date;

/**
 * AuthorityControlVersion
 */
public class AuthorityControlVersion implements java.io.Serializable {


    private Integer id;
    private Date changeDate;
    private int changeUserId;
    private int acid;
    private Date date;
    private int fdid;
    private String value;
    private String code;
    private int userId;
    private String action;

    public AuthorityControlVersion() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getChangeDate() {
        return this.changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public int getChangeUserId() {
        return this.changeUserId;
    }

    public void setChangeUserId(int changeUserId) {
        this.changeUserId = changeUserId;
    }

    public int getAcid() {
        return this.acid;
    }

    public void setAcid(int acid) {
        this.acid = acid;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFdid() {
        return this.fdid;
    }

    public void setFdid(int fdid) {
        this.fdid = fdid;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }


}


