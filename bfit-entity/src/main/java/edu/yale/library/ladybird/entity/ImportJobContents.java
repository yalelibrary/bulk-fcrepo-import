package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportJobContents implements java.io.Serializable {

    private int id;
    private int importId;
    private Date date;
    private int row;
    private int col;
    private String value;
    @Deprecated
    private String complete;

    public ImportJobContents() {
    }

    @Deprecated
    public ImportJobContents(int id, int importId, Date date, int row, int col, String complete) {
        this.id = id;
        this.importId = importId;
        this.date = date;
        this.row = row;
        this.col = col;
        this.complete = complete;
    }

    public ImportJobContents(int importId, Date date, int row, int col, String value) {
        this.importId = importId;
        this.date = date;
        this.row = row;
        this.col = col;
        this.value = value;
    }

    @Deprecated
    public ImportJobContents(int id, int importId, Date date, int row, int col, String value, String complete) {
        this.id = id;
        this.importId = importId;
        this.date = date;
        this.row = row;
        this.col = col;
        this.value = value;
        this.complete = complete;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
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

    public int getRow() {
        return this.row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Deprecated
    public String getComplete() {
        return this.complete;
    }

    @Deprecated
    public void setComplete(String complete) {
        this.complete = complete;
    }

    @Override
    public String toString() {
        return "ImportJobContents{"
                + "importId=" + importId
                + ", row=" + row
                + ", col=" + col
                + ", value='" + value + '\''
                + '}';
    }
}


