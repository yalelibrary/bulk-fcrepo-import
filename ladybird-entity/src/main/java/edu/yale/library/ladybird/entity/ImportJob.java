package edu.yale.library.ladybird.entity;


import java.util.Date;


public class ImportJob implements java.io.Serializable {

    private Integer importId;
    private Date date;
    private int userId;
    private String jobFile;

    private String jobDirectory;

    public ImportJob() {
    }

    public ImportJob(Date date, int userId, String jobFile, String jobDirectory) {
        this.date = date;
        this.userId = userId;
        this.jobFile = jobFile;
        this.jobDirectory = jobDirectory;
    }

    public Integer getImportId() {
        return this.importId;
    }

    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getJobFile() {
        return this.jobFile;
    }

    public void setJobFile(String jobFile) {
        this.jobFile = jobFile;
    }

    public String getJobDirectory() {
        return this.jobDirectory;
    }

    public void setJobDirectory(String jobDirectory) {
        this.jobDirectory = jobDirectory;
    }

    @Override
    public String toString() {
        return "ImportJob{"
                + "importId=" + importId
                + ", date=" + date
                + ", userId=" + userId
                + ", jobFile='" + jobFile
                + '\''
                + ", jobDirectory='"
                + jobDirectory
                + '\''
                +
                '}';
    }
}


