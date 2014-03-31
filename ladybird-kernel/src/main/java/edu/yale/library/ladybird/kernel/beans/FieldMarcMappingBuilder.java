package edu.yale.library.ladybird.kernel.beans;

import java.util.Date;

public class FieldMarcMappingBuilder {
    private int id;
    private Date date;
    private String k1;
    private String k2;
    private String concat;
    private String delim;
    private Integer fdid;

    public FieldMarcMappingBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public FieldMarcMappingBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public FieldMarcMappingBuilder setK1(String k1) {
        this.k1 = k1;
        return this;
    }

    public FieldMarcMappingBuilder setK2(String k2) {
        this.k2 = k2;
        return this;
    }

    public FieldMarcMappingBuilder setConcat(String concat) {
        this.concat = concat;
        return this;
    }

    public FieldMarcMappingBuilder setDelim(String delim) {
        this.delim = delim;
        return this;
    }

    public FieldMarcMappingBuilder setFdid(Integer fdid) {
        this.fdid = fdid;
        return this;
    }

    public FieldMarcMapping createFieldMarcMapping() {
        return new FieldMarcMapping(id);
    }
}