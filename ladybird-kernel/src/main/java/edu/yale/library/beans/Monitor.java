package edu.yale.library.beans;// default package


/**
 * Monitor
 */
public class Monitor implements java.io.Serializable
{


    private Integer id;
    private String dirPath;

    public Monitor()
    {

    }

    public Monitor(Integer id, String dirPath)
    {
        this.id = id;
        this.dirPath = dirPath;
    }

    public String getDirPath()
    {
        return dirPath;
    }

    public void setDirPath(String dirPath)
    {
        this.dirPath = dirPath;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }
}


