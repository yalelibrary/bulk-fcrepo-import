package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.entity.JobRequest;

import java.util.Collections;
import java.util.List;

/**
 * Used by Export related artifacts.
 * @see edu.yale.library.ladybird.engine.imports.ImportJobRequest for a related class
 */
public class ImportEntityContext {

    private List<Import.Row> importJobList; //TODO rename
    private JobRequest jobRequest;
    /** imj id */
    private int importId;

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public void setJobRequest(final JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }

    public List<Import.Row> getImportJobList() {
        return importJobList;
    }

    public void setImportJobList(final List<Import.Row> importJobList) {
        this.importJobList = importJobList;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    /**
     * Instantiates empty instance
     * @return
     */
    public static ImportEntityContext newInstance() {
        ImportEntityContext importEntityContext = new ImportEntityContext();
        importEntityContext.setImportJobList(Collections.emptyList());
        importEntityContext.setJobRequest(new JobRequest());
        importEntityContext.setImportId(-1);
        return importEntityContext;
    }

    @Override
    public String toString() {
        return "ImportEntityContext{"
                + "importJobList size=" + importJobList.size()
                + ", monitor=" + jobRequest
                + '}';
    }
}
