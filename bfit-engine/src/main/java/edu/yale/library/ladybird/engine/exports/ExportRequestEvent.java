package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.entity.JobRequest;
import edu.yale.library.ladybird.entity.event.ExportEvent;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ExportRequestEvent extends ExportEvent {

    /**
     * Import Job identifier (aka imid) to run against
     */
    private int importId;

    private JobRequest jobRequest = new JobRequest();

    public ExportRequestEvent() {
    }

    public ExportRequestEvent(int importId) {
        this.importId = importId;
    }

    public ExportRequestEvent(final int importId, final JobRequest jobRequest) {
        this.importId = importId;
        this.jobRequest = jobRequest;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public void setJobRequest(final JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }

    @Override
    public String toString() {
        return "ExportRequestEvent{"
                + "importId=" + importId
                + ", monitor=" + jobRequest
                + '}';
    }
}
