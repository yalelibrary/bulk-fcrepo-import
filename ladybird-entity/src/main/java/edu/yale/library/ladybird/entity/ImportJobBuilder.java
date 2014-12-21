package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ImportJobBuilder {
    private Date date;
    private int userId;
    private String jobFile;
    private String jobDirectory;
    private String exportJobDir;
    private String exportJobFile;
    private Integer importId;
    private Integer requestId;

    public ImportJobBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ImportJobBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public ImportJobBuilder setJobFile(String jobFile) {
        this.jobFile = jobFile;
        return this;
    }

    public ImportJobBuilder setJobDirectory(String jobDirectory) {
        this.jobDirectory = jobDirectory;
        return this;
    }

    public ImportJobBuilder setExportJobDir(String exportJobDir) {
        this.exportJobDir = exportJobDir;
        return this;
    }

    public ImportJobBuilder setExportJobFile(String exportJobFile) {
        this.exportJobFile = exportJobFile;
        return this;
    }

    public ImportJobBuilder setImportId(Integer importId) {
        this.importId = importId;
        return this;
    }

    public ImportJobBuilder setRequestId(Integer id) {
        this.requestId = id;
        return this;
    }

    public ImportJob createImportJob() {
        return new ImportJob(date, userId, jobFile, jobDirectory, exportJobFile, exportJobDir, requestId);
    }
}