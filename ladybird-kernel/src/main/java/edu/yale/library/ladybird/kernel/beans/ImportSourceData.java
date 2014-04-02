package edu.yale.library.ladybird.kernel.beans;


import java.util.Date;

public class ImportSourceData implements java.io.Serializable {


    private Integer id;
    private Date date;
    private int importSourceId;
    private String k1;
    private String k2;
    private String k3;
    private String attr;
    private String attrVal;
    private String value;
    private Integer zindex;
    private String notes;

    public ImportSourceData() {
    }


    public ImportSourceData(Integer id, Date date, int importSourceId, String k1, String k2, String k3,
                            String attr, String attrVal, String value, Integer zindex, String notes) {
        this.id = id;
        this.date = date;
        this.importSourceId = importSourceId;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.attr = attr;
        this.attrVal = attrVal;
        this.value = value;
        this.zindex = zindex;
        this.notes = notes;
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

    public int getImportSourceId() {
        return this.importSourceId;
    }

    public void setImportSourceId(int importSourceId) {
        this.importSourceId = importSourceId;
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

    public String getK3() {
        return this.k3;
    }

    public void setK3(String k3) {
        this.k3 = k3;
    }

    public String getAttr() {
        return this.attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getAttrVal() {
        return this.attrVal;
    }

    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getZindex() {
        return this.zindex;
    }

    public void setZindex(Integer zindex) {
        this.zindex = zindex;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ImportSourceData{"
                + "importSourceId=" + importSourceId
                + ", k1='" + k1 + '\''
                + ", k2='" + k2 + '\''
                + ", value='" + value + '\''
                + '}';
    }
}


