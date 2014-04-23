package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.kernel.model.Monitor;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;

/**
 * TODO field access. Subject to modification.
 */
public final class ImportRequestEvent extends ImportEvent {

    private SpreadsheetFile spreadsheetFile;
    private Monitor monitor;

    public ImportRequestEvent(SpreadsheetFile spreadsheetFile, Monitor monitor) {
        this.spreadsheetFile = spreadsheetFile;
        this.monitor = monitor;
    }

    public SpreadsheetFile getSpreadsheetFile() {
        return spreadsheetFile;
    }

    @Override
    public String toString() {
        return "ImportRequestEvent{" + "spreadsheetFile=" + spreadsheetFile
                + ", monitor=" + monitor + '}';
    }

    public void setSpreadsheetFile(SpreadsheetFile spreadsheetFile) {
        this.spreadsheetFile = spreadsheetFile;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
}
