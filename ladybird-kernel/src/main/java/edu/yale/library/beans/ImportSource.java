package edu.yale.library.beans;


import java.util.Date;

public class ImportSource implements java.io.Serializable
{


    private Integer importSourceId;
    private Date date;
    private String sourceid;
    private Integer importId;
    private Integer col;
    private Integer row;
    private String schema;
    private String url;

    public ImportSource()
    {
    }


    public ImportSource(Date date)
    {
        this.date = date;
    }

    public ImportSource(Date date, String sourceid, Integer importId, Integer col, Integer row, String schema, String url)
    {
        this.date = date;
        this.sourceid = sourceid;
        this.importId = importId;
        this.col = col;
        this.row = row;
        this.schema = schema;
        this.url = url;
    }

    public Integer getImportSourceId()
    {
        return this.importSourceId;
    }

    public void setImportSourceId(Integer importSourceId)
    {
        this.importSourceId = importSourceId;
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getSourceid()
    {
        return this.sourceid;
    }

    public void setSourceid(String sourceid)
    {
        this.sourceid = sourceid;
    }

    public Integer getImportId()
    {
        return this.importId;
    }

    public void setImportId(Integer importId)
    {
        this.importId = importId;
    }

    public Integer getCol()
    {
        return this.col;
    }

    public void setCol(Integer col)
    {
        this.col = col;
    }

    public Integer getRow()
    {
        return this.row;
    }

    public void setRow(Integer row)
    {
        this.row = row;
    }

    public String getSchema()
    {
        return this.schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }


}


