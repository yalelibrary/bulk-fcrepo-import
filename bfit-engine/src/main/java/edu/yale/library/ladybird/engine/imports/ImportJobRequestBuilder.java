package edu.yale.library.ladybird.engine.imports;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportJobRequestBuilder {

    private int userId;

    private Date date;

    private String jobFile;

    private String jobDir;

    private int projectId;

    private int requestId;

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

    public ImportJobRequestBuilder projectId(int projectId) {
        this.projectId = projectId;
        return this;
    }

    public ImportJobRequestBuilder requestId(int requestId) {
        this.requestId = requestId;
        return this;
    }

    public ImportJobRequest build() {
        return new ImportJobRequest(userId, date, jobFile, jobDir, projectId, requestId);
    }
}