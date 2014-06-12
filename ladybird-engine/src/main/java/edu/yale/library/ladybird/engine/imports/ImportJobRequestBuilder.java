package edu.yale.library.ladybird.engine.imports;

import java.util.Date;

public class ImportJobRequestBuilder {
    private int userId;
    private Date date;
    private String jobFile;
    private String jobDir;

    public ImportJobRequestBuilder userId(int userId) {
        this.userId = userId;
        return this;
    }

    public ImportJobRequestBuilder date(Date date) {
        this.date = date;
        return this;
    }

    public ImportJobRequestBuilder file(String jobFile) {
        this.jobFile = jobFile;
        return this;
    }

    public ImportJobRequestBuilder dir(String jobDir) {
        this.jobDir = jobDir;
        return this;
    }

    public ImportJobRequest build() {
        return new ImportJobRequest(userId, date, jobFile, jobDir);
    }
}