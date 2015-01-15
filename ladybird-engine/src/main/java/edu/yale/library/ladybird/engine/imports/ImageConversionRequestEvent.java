package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.event.ImportEvent;

public class ImageConversionRequestEvent extends ImportEvent {

    private int importId;

    private String exportDirPath = "";

    //TODO use just column?
    private ImportValue importValue;

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

    public ImportValue getImportValue() {
        return importValue;
    }

    public void setImportValue(ImportValue importValue) {
        this.importValue = importValue;
    }

    @Override
    public String toString() {
        return "ImageConversionRequestEvent{" + "importId=" + importId
                + ", exportDirPath='" + exportDirPath + '\'' + '}';
    }
}
