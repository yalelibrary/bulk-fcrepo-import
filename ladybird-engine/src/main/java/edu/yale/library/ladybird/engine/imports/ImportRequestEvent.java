package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.JobRequest;
import edu.yale.library.ladybird.entity.event.ImportEvent;

/**
 * TODO field access. Subject to modification.
 */
public final class ImportRequestEvent extends ImportEvent {

    private Spreadsheet spreadsheet;

    private JobRequest jobRequest;

    public ImportRequestEvent(Spreadsheet spreadsheet, JobRequest jobRequest) {
        this.spreadsheet = spreadsheet;
        this.jobRequest = jobRequest;
    }

    public Spreadsheet getSpreadsheet() {
        return spreadsheet;
    }

    @Override
    public String toString() {
        return "ImportRequestEvent{" + "spreadsheetFile=" + spreadsheet
                + ", jobRequest=" + jobRequest + '}';
    }

    public void setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public void setJobRequest(JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }
}
