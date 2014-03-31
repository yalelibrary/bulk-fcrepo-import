package edu.yale.library.ladybird.engine.imports;


import java.io.InputStream;

/**
 * Represents a spreadsheet file. Subject to modification. Simple implementation for now.
 */
public class SpreadsheetFile implements Cloneable {
    private String fileName;

    private String altName;

    private String path;

    private InputStream fileStream;

    public SpreadsheetFile(String fileName, String altName, String path, InputStream fileStream) {
        this.fileName = fileName;
        this.altName = altName;
        this.path = path;
        this.fileStream = fileStream;
    }

    public InputStream getFileStream() {
        return fileStream;
    }

    public void setFileStream(InputStream fileStream) {
        this.fileStream = fileStream;
    }

    public String getAltName() {
        return altName;
    }

    public void setAltName(String altName) {
        this.altName = altName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {

        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "SpreadsheetFile{"
                + "fileName='" + fileName + '\''
                + ", altName='" + altName + '\''
                + ", path='" + path + '\''
                + '}';
    }

    @Override
    protected SpreadsheetFile clone() {
        try {
            return (SpreadsheetFile) super.clone();
        } catch (Throwable t) {
            throw new InternalError(t.toString());
        }
    }

}
