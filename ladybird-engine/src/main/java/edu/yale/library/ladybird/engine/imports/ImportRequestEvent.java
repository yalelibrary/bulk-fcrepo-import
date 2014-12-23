package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;

/**
 * TODO field access. Subject to modification.
 */
public final class ImportRequestEvent extends ImportEvent {

    private Spreadsheet spreadsheet;
    private Monitor monitor;

    public ImportRequestEvent(Spreadsheet spreadsheet, Monitor monitor) {
        this.spreadsheet = spreadsheet;
        this.monitor = monitor;
    }

    public Spreadsheet getSpreadsheet() {
        return spreadsheet;
    }

    @Override
    public String toString() {
        return "ImportRequestEvent{" + "spreadsheetFile=" + spreadsheet
                + ", monitor=" + monitor + '}';
    }

    public void setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
}
