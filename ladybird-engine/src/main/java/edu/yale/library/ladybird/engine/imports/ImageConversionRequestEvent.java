package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;

public class ImageConversionRequestEvent extends ImportEvent {

    private int importId;

    //private Monitor monitor = new Monitor();

    private String exportDirPath = "";

    //TODO use just column?
    private ImportEntityValue importEntityValue;

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    //TODO check
    @Override
    public String getPrincipal() {
        return "auto";
    }

    public String getExportDirPath() {
        return exportDirPath;
    }

    public void setExportDirPath(String exportDirPath) {
        this.exportDirPath = exportDirPath;
    }

    public ImportEntityValue getImportEntityValue() {
        return importEntityValue;
    }

    public void setImportEntityValue(ImportEntityValue importEntityValue) {
        this.importEntityValue = importEntityValue;
    }

    @Override
    public String toString() {
        return "ImageConversionRequestEvent{" +
                "importId=" + importId +
                ", exportDirPath='" + exportDirPath + '\'' +
                '}';
    }
}
