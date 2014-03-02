package edu.yale.library.engine.exports;

public class ExportRequestEvent {

    /**
     * Import Job identifier (aka imid) to run against
     */
    private int importId;

    public ExportRequestEvent(int importId) {
        this.importId = importId;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    @Override
    public String toString() {
        return "ExportRequestEvent{" + "importId=" + importId + '}';
    }
}
