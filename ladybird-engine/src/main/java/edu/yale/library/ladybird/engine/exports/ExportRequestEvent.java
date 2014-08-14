package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.kernel.events.exports.ExportEvent;

public class ExportRequestEvent extends ExportEvent {

    /**
     * Import Job identifier (aka imid) to run against
     */
    private int importId;
    private Monitor monitor = new Monitor();

    public ExportRequestEvent() {
    }

    public ExportRequestEvent(int importId) {
        this.importId = importId;
    }

    public ExportRequestEvent(final int importId, final Monitor monitor) {
        this.importId = importId;
        this.monitor = monitor;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(final Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public String toString() {
        return "ExportRequestEvent{"
                + "importId=" + importId
                + ", monitor=" + monitor
                + '}';
    }
}
