package edu.yale.library.beans;

import java.util.Date;

public class ImportJobContentsBuilder
{
    private int importId;
    private Date date;
    private int row;
    private int col;
    private String complete;
    private String value;

    public ImportJobContentsBuilder setImportId(int importId)
    {
        this.importId = importId;
        return this;
    }

    public ImportJobContentsBuilder setDate(Date date)
    {
        this.date = date;
        return this;
    }

    public ImportJobContentsBuilder setRow(int row)
    {
        this.row = row;
        return this;
    }

    public ImportJobContentsBuilder setCol(int col)
    {
        this.col = col;
        return this;
    }
    @Deprecated
    public ImportJobContentsBuilder setComplete(String complete)
    {
        this.complete = complete;
        return this;
    }

    public ImportJobContentsBuilder setValue(String value)
    {
        this.value = value;
        return this;
    }

    public ImportJobContents build()
    {
        return new ImportJobContents(importId, date, row, col, value);
    }
}