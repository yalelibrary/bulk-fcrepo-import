package edu.yale.library.ladybird.entity;


import java.util.Date;


public class ImportJob implements java.io.Serializable {

    private Integer importId;
    private Date date;
    private int userId;
    private String jobFile;
    private String jobDirectory;
    private String exportJobFile;
    private String exportJobDir;
    private int requestId;

    public ImportJob() {
    }

    public ImportJob(Date date, int userId, String jobFile, String jobDirectory, String exportJobFile, String exportJobDir, int requestId) {
        this.date = date;
        this.userId = userId;
        this.jobFile = jobFile;
        this.jobDirectory = jobDirectory;
        this.exportJobFile = exportJobFile;
        this.exportJobDir = exportJobDir;
        this.requestId = requestId;
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

    public String getExportJobDir() {
        return exportJobDir;
    }

    public void setExportJobDir(String exportJobDir) {
        this.exportJobDir = exportJobDir;
    }

    public String getExportJobFile() {
        return exportJobFile;
    }

    public void setExportJobFile(String exportJobFile) {
        this.exportJobFile = exportJobFile;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "ImportJob{"
                + "date=" + date
                + ", importId=" + importId
                + ", userId=" + userId
                + ", jobFile='" + jobFile + '\''
                + ", jobDirectory='" + jobDirectory + '\''
                + ", exportJobFile='" + exportJobFile + '\''
                + ", exportJobDir='" + exportJobDir + '\''
                + '}';
    }
}


