package edu.yale.library.engine.imports;

import java.util.Date;

public class ImportJobContextBuilder {
    private int userId;
    private Date date;
    private String jobFile;
    private String jobDir;

    public ImportJobContextBuilder userId(int userId) {
        this.userId = userId;
        return this;
    }

    public ImportJobContextBuilder date(Date date) {
        this.date = date;
        return this;
    }

    public ImportJobContextBuilder file(String jobFile) {
        this.jobFile = jobFile;
        return this;
    }

    public ImportJobContextBuilder dir(String jobDir) {
        this.jobDir = jobDir;
        return this;
    }

    public ImportJobContext build() {
        return new ImportJobContext(userId, date, jobFile, jobDir);
    }
}