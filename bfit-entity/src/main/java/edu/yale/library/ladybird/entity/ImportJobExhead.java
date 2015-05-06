package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportJobExhead implements java.io.Serializable {

    private Integer id;
    private int importId;
    private Date date;
    private String value;
    private Integer col;
    @Deprecated
    private String func;
    @Deprecated
    private Integer fdid;

    @Deprecated
    private String import_;

    public ImportJobExhead() {
    }

    @Deprecated
    public ImportJobExhead(int importId, Date date, Integer col, String value) {
        this.importId = importId;
        this.date = date;
        this.col = col;
        this.value = value;
    }

    @Deprecated
    public ImportJobExhead(int importId, Date date, String value, Integer col, String func, Integer fdid, String import_) {
        this.importId = importId;
        this.date = date;
        this.value = value;
        this.col = col;
        this.func = func;
        this.fdid = fdid;
        this.import_ = import_;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getImportId() {
        return this.importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCol() {
        return this.col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    @Deprecated
    public String getFunc() {
        return this.func;
    }

    @Deprecated
    public void setFunc(String func) {
        this.func = func;
    }

    @Deprecated
    public Integer getFdid() {
        return this.fdid;
    }

    @Deprecated
    public void setFdid(Integer fdid) {
        this.fdid = fdid;
    }

    @Deprecated
    public String getImport_() {
        return this.import_;
    }

    @Deprecated
    public void setImport_(String import_) {
        this.import_ = import_;
    }

    @Override
    public String toString() {
        return "ImportJobExhead{"
                + "col=" + col
                //+ ", date=" + date
                + ", importId=" + importId
                + ", value='" + value + '\''
                + '}';
    }

}