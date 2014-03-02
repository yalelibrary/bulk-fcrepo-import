package edu.yale.library.engine.imports;


import java.util.Date;

public final class ImportJobContext {
    private int userId;
    private Date date; //actual deposit date
    private String jobFile;
    private String jobDir;

    public ImportJobContext(int userId, Date date, String jobFile, String jobDir) {
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
