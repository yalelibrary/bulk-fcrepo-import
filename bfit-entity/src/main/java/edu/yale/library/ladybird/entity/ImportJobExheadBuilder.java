package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportJobExheadBuilder {

    private int importId;
    private Date date;
    private String value;
    private Integer col;
    private String func;
    private Integer fdid;
    private String import_;

    public ImportJobExheadBuilder setImportId(int importId) {
        this.importId = importId;
        return this;
    }

    public ImportJobExheadBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ImportJobExheadBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public ImportJobExheadBuilder setCol(Integer col) {
        this.col = col;
        return this;
    }

    public ImportJobExheadBuilder setFunc(String func) {
        this.func = func;
        return this;
    }

    public ImportJobExheadBuilder setFdid(Integer fdid) {
        this.fdid = fdid;
        return this;
    }

    public ImportJobExheadBuilder setImport_(String import_) {
        this.import_ = import_;
        return this;
    }

    public ImportJobExhead createImportJobExhead() {
        return new ImportJobExhead(importId, date, col, value);
    }
}