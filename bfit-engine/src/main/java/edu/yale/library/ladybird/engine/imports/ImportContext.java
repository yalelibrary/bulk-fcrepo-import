package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.JobRequest;

import java.util.Collections;
import java.util.List;

/**
 * Used by Export related artifacts.
 * @author Osman Din
 * @see edu.yale.library.ladybird.engine.imports.ImportJobRequest for a related class
 */
public class ImportContext {

    private List<Import.Row> importRowsList;

    private JobRequest jobRequest;

    /** import job id */
    private int importId;

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public void setJobRequest(final JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }

    public List<Import.Row> getImportRowsList() {
        return importRowsList;
    }

    public void setImportRowsList(final List<Import.Row> importRowsList) {
        this.importRowsList = importRowsList;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    /**
     * Instantiates empty instance
     */
    public static ImportContext newInstance() {
        ImportContext importContext = new ImportContext();
        importContext.setImportRowsList(Collections.emptyList());
        importContext.setJobRequest(new JobRequest());
        importContext.setImportId(-1);
        return importContext;
    }

    @Override
    public String toString() {
        return "ImportEntityContext{"
                + "importRowsList size=" + importRowsList.size()
                + ", monitor=" + jobRequest
                + '}';
    }
}
