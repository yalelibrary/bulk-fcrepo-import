package edu.yale.library.entity.model;

import java.util.Date;

public class ImportSourceDataBuilder {
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

    public ImportSourceDataBuilder setId(Integer id) {
        this.id = id;
        return this;
    }

    public ImportSourceDataBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ImportSourceDataBuilder setImportSourceId(int importSourceId) {
        this.importSourceId = importSourceId;
        return this;
    }

    public ImportSourceDataBuilder setK1(String k1) {
        this.k1 = k1;
        return this;
    }

    public ImportSourceDataBuilder setK2(String k2) {
        this.k2 = k2;
        return this;
    }

    public ImportSourceDataBuilder setK3(String k3) {
        this.k3 = k3;
        return this;
    }

    public ImportSourceDataBuilder setAttr(String attr) {
        this.attr = attr;
        return this;
    }

    public ImportSourceDataBuilder setAttrVal(String attrVal) {
        this.attrVal = attrVal;
        return this;
    }

    public ImportSourceDataBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public ImportSourceDataBuilder setZindex(Integer zindex) {
        this.zindex = zindex;
        return this;
    }

    public ImportSourceDataBuilder setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public ImportSourceData createImportSourceData() {
        return new ImportSourceData(id, date, importSourceId, k1, k2, k3, attr, attrVal, value, zindex, notes);
    }
}