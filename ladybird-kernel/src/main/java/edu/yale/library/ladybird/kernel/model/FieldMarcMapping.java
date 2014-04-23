package edu.yale.library.ladybird.kernel.model;

import java.util.Date;


public class FieldMarcMapping implements java.io.Serializable {

    private int id;
    private Date date;
    private String k1;
    private String k2;
    private String concat;
    private String delim;
    private Integer fdid;

    public FieldMarcMapping() {
    }


    public FieldMarcMapping(int id) {
        this.id = id;
    }

    public FieldMarcMapping(int id, Date date, String k1, String k2, String concat, String delim, Integer fdid) {
        this.id = id;
        this.date = date;
        this.k1 = k1;
        this.k2 = k2;
        this.concat = concat;
        this.delim = delim;
        this.fdid = fdid;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getK1() {
        return this.k1;
    }

    public void setK1(String k1) {
        this.k1 = k1;
    }

    public String getK2() {
        return this.k2;
    }

    public void setK2(String k2) {
        this.k2 = k2;
    }

    public String getConcat() {
        return this.concat;
    }

    public void setConcat(String concat) {
        this.concat = concat;
    }

    public String getDelim() {
        return this.delim;
    }

    public void setDelim(String delim) {
        this.delim = delim;
    }

    public Integer getFdid() {
        return this.fdid;
    }

    public void setFdid(Integer fdid) {
        this.fdid = fdid;
    }

    @Override
    public String toString() {
        return "FieldMarcMapping{"
                + "id=" + id
                + ", date=" + date
                + ", k1='" + k1 + '\''
                + ", k2='" + k2 + '\''
                + ", concat='" + concat + '\''
                + ", delim='" + delim + '\''
                + ", fdid=" + fdid
                + '}';
    }
}


