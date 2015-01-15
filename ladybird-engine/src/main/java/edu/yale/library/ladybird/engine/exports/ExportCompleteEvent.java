package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.Spreadsheet;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.event.ExportEvent;

/**
 * TODO field access. Subject to modification.
 */
public final class ExportCompleteEvent extends ExportEvent {

    private int rowsProcessed;

    private int passCount;

    private int failCount;

    private int failedValidations;

    private Spreadsheet spreadsheet;

    private int importId;

    private long time;

    public ExportCompleteEvent(User user, Spreadsheet spreadsheet, int rowsProcessed, int passCount,
                               int failedValidations, int failCount, long time) {
        this.spreadsheet = spreadsheet;
        this.rowsProcessed = rowsProcessed;
        this.passCount = passCount;
        this.failedValidations = failedValidations;
        this.failCount = failCount;
        this.time = time;
    }

    public int getRowsProcessed() {
        return rowsProcessed;
    }

    public int getPassCount() {
        return passCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public int getFailedValidations() {
        return failedValidations;
    }

    public Spreadsheet getSpreadsheet() {
        return spreadsheet;
    }

    public void setRowsProcessed(int rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public void setFailedValidations(int failedValidations) {
        this.failedValidations = failedValidations;
    }

    public void setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

    public long getTime() {
        return time;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    @Override
    public String toString() {
        return "ExportCompleteEvent{"
                + "rowsProcessed=" + rowsProcessed
                + ", passCount=" + passCount
                + ", failCount=" + failCount
                + ", failedValidations=" + failedValidations
                + ", spreadsheetFile=" + spreadsheet
                + '}';
    }
}
