package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.entity.Monitor;

import java.util.List;

public class ImportJobCtx {

    private List<ImportEntity.Row> importJobList;
    private Monitor monitor;

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
}
