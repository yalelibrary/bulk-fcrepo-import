package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.kernel.events.exports.ExportEvent;

public class ExportRequestEvent extends ExportEvent {

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
