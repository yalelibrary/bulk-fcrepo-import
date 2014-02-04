package edu.yale.library.engine.imports;


/**
 * Represents a spreadsheet file. Subject to modification. Simple implementation for now.
 */
public class SpreadsheetFile implements Cloneable
{
    private String fileName;

    private String altName;

    private String path;

    public String getAltName()
    {
        return altName;
    }

    public void setAltName(String altName)
    {
        this.altName = altName;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getFileName()
    {

        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    @Override
    public String toString()
    {
        return "SpreadsheetFile{" +
                "fileName='" + fileName + '\'' +
                ", altName='" + altName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    protected SpreadsheetFile clone()
    {
        try
        {
            return (SpreadsheetFile) super.clone();
        }
        catch (Throwable t)
        {
            throw new InternalError(t.toString());
        }
    }

}
