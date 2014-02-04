package edu.yale.library.beans;


import java.util.Date;


public class ImportFile implements java.io.Serializable
{


    private Integer id;
    private Integer importId;
    private Date date;
    private String fileLocation;
    private int oid;
    private int userId;
    private Integer code;
    private String error;
    private String type;
    private String label;
    public ImportFile()
    {
    }

    public ImportFile(Integer importId, Date date, String fileLocation, int oid, int userId)
    {
        this.importId = importId;
        this.date = date;
        this.fileLocation = fileLocation;
        this.oid = oid;
        this.userId = userId;
    }


    public ImportFile(Integer importId, Date date, String fileLocation, int oid, int userId, Integer code, String error, String type, String label)
    {
        this.importId = importId;
        this.date = date;
        this.fileLocation = fileLocation;
        this.oid = oid;
        this.userId = userId;
        this.code = code;
        this.error = error;
        this.type = type;
        this.label = label;
    }

    public Integer getImportId()
    {
        return importId;
    }

    public void setImportId(Integer importId)
    {
        this.importId = importId;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer ImportId()
    {
        return this.importId;
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getFileLocation()
    {
        return this.fileLocation;
    }

    public void setFileLocation(String fileLocation)
    {
        this.fileLocation = fileLocation;
    }

    public int getOid()
    {
        return this.oid;
    }

    public void setOid(int oid)
    {
        this.oid = oid;
    }

    public int getUserId()
    {
        return this.userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public Integer getCode()
    {
        return this.code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public String getError()
    {
        return this.error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getLabel()
    {
        return this.label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }



}


