package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class AccessconditionType implements java.io.Serializable {


    private Integer accessconditionTypeId;
    private Date date;
    private int userId;
    private String label;

    public AccessconditionType() {
    }

    public AccessconditionType(Date date, int userId, String label) {
        this.date = date;
        this.userId = userId;
        this.label = label;
    }

    public Integer getAccessconditionTypeId() {
        return this.accessconditionTypeId;
    }

    public void setAccessconditionTypeId(Integer accessconditionTypeId) {
        this.accessconditionTypeId = accessconditionTypeId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "AccessconditionType{"
                + "accessconditionTypeId=" + accessconditionTypeId
                + ", date=" + date
                + ", userId=" + userId + ", label='" + label + '\''
                + '}';
    }
}


