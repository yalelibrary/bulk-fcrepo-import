package edu.yale.library.ladybird.engine.imports;


import java.util.Date;

/**
 * Used by ImportWriter to represent import job request
 */
public final class ImportJobRequest {
    private int userId;
    private Date date;
    private String jobFile;
    private String jobDir;

    public ImportJobRequest(int userId, Date date, String jobFile, String jobDir) {
        this.userId = userId;
        this.date = date;
        this.jobFile = jobFile;
        this.jobDir = jobDir;
    }

    public int getUserId() {
        return userId;
    }

    public Date getDate() {
        return date;
    }

    public String getJobFile() {
        return jobFile;
    }

    public String getJobDir() {
        return jobDir;
    }
}
