package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.entity.Monitor;

import java.util.Collections;
import java.util.List;

/**
 * Used by Export related artifacts.
 * @see edu.yale.library.ladybird.engine.imports.ImportJobRequest for a related class
 */
public class ImportEntityContext {

    private List<ImportEntity.Row> importJobList;
    private Monitor monitor;
    /** imj id */
    private int importId;

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(final Monitor monitor) {
        this.monitor = monitor;
    }

    public List<ImportEntity.Row> getImportJobList() {
        return importJobList;
    }

    public void setImportJobList(final List<ImportEntity.Row> importJobList) {
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
        importEntityContext.setMonitor(new Monitor());
        importEntityContext.setImportId(-1);
        return importEntityContext;
    }

    @Override
    public String toString() {
        return "ImportEntityContext{"
                + "importJobList size=" + importJobList.size()
                + ", monitor=" + monitor
                + '}';
    }
}
