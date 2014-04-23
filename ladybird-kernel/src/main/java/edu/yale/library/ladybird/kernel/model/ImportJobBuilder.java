package edu.yale.library.ladybird.kernel.model;

import java.util.Date;

public class ImportJobBuilder {
    private Date date;
    private int userId;
    private String jobFile;
    private String jobDirectory;

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

    public ImportJob createImportJob() {
        return new ImportJob(date, userId, jobFile, jobDirectory);
    }
}