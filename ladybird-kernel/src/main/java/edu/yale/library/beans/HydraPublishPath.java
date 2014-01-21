package edu.yale.library.beans;// default package


import java.util.Date;

/**
 * HydraPublishPath
 */
public class HydraPublishPath implements java.io.Serializable
{


    private Integer id;
    private Date date;
    private int hydraPublishId;
    private String type;
    private String pathhttp;
    private String pathunc;
    private String md5;
    private String sha256;
    private String controlgroup;
    private String mimetype;
    private String dsid;
    private String ingestmethod;
    private String altids;
    private String dslabel;
    private Integer versionable;
    private String dsstate;
    private String logmessage;
    private Integer oidpointer;
    private Integer fileSize;
    private String fileExtension;

    public HydraPublishPath()
    {
    }


    public HydraPublishPath(Date date, int hydraPublishId)
    {
        this.date = date;
        this.hydraPublishId = hydraPublishId;
    }

    public HydraPublishPath(Date date, int hydraPublishId, String type, String pathhttp, String pathunc, String md5, String sha256, String controlgroup, String mimetype, String dsid, String ingestmethod, String altids, String dslabel, Integer versionable, String dsstate, String logmessage, Integer oidpointer, Integer fileSize, String fileExtension)
    {
        this.date = date;
        this.hydraPublishId = hydraPublishId;
        this.type = type;
        this.pathhttp = pathhttp;
        this.pathunc = pathunc;
        this.md5 = md5;
        this.sha256 = sha256;
        this.controlgroup = controlgroup;
        this.mimetype = mimetype;
        this.dsid = dsid;
        this.ingestmethod = ingestmethod;
        this.altids = altids;
        this.dslabel = dslabel;
        this.versionable = versionable;
        this.dsstate = dsstate;
        this.logmessage = logmessage;
        this.oidpointer = oidpointer;
        this.fileSize = fileSize;
        this.fileExtension = fileExtension;
    }

    public Integer getId()
    {
        return this.id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Date getDate()
    {
        return this.date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getHydraPublishId()
    {
        return this.hydraPublishId;
    }

    public void setHydraPublishId(int hydraPublishId)
    {
        this.hydraPublishId = hydraPublishId;
    }

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPathhttp()
    {
        return this.pathhttp;
    }

    public void setPathhttp(String pathhttp)
    {
        this.pathhttp = pathhttp;
    }

    public String getPathunc()
    {
        return this.pathunc;
    }

    public void setPathunc(String pathunc)
    {
        this.pathunc = pathunc;
    }

    public String getMd5()
    {
        return this.md5;
    }

    public void setMd5(String md5)
    {
        this.md5 = md5;
    }

    public String getSha256()
    {
        return this.sha256;
    }

    public void setSha256(String sha256)
    {
        this.sha256 = sha256;
    }

    public String getControlgroup()
    {
        return this.controlgroup;
    }

    public void setControlgroup(String controlgroup)
    {
        this.controlgroup = controlgroup;
    }

    public String getMimetype()
    {
        return this.mimetype;
    }

    public void setMimetype(String mimetype)
    {
        this.mimetype = mimetype;
    }

    public String getDsid()
    {
        return this.dsid;
    }

    public void setDsid(String dsid)
    {
        this.dsid = dsid;
    }

    public String getIngestmethod()
    {
        return this.ingestmethod;
    }

    public void setIngestmethod(String ingestmethod)
    {
        this.ingestmethod = ingestmethod;
    }

    public String getAltids()
    {
        return this.altids;
    }

    public void setAltids(String altids)
    {
        this.altids = altids;
    }

    public String getDslabel()
    {
        return this.dslabel;
    }

    public void setDslabel(String dslabel)
    {
        this.dslabel = dslabel;
    }

    public Integer getVersionable()
    {
        return this.versionable;
    }

    public void setVersionable(Integer versionable)
    {
        this.versionable = versionable;
    }

    public String getDsstate()
    {
        return this.dsstate;
    }

    public void setDsstate(String dsstate)
    {
        this.dsstate = dsstate;
    }

    public String getLogmessage()
    {
        return this.logmessage;
    }

    public void setLogmessage(String logmessage)
    {
        this.logmessage = logmessage;
    }

    public Integer getOidpointer()
    {
        return this.oidpointer;
    }

    public void setOidpointer(Integer oidpointer)
    {
        this.oidpointer = oidpointer;
    }

    public Integer getFileSize()
    {
        return this.fileSize;
    }

    public void setFileSize(Integer fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getFileExtension()
    {
        return this.fileExtension;
    }

    public void setFileExtension(String fileExtension)
    {
        this.fileExtension = fileExtension;
    }


}


