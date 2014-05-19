package edu.yale.library.ladybird.engine.imports;

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

    @Override
    public String toString() {
        return "ImportJobCtx{"
                + "importJobList size=" + importJobList.size()
                + ", monitor=" + monitor
                + '}';
    }
}
