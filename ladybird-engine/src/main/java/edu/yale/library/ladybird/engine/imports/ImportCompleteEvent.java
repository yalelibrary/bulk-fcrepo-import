package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;

/**
 * TODO field access. Subject to modification.
 */
public final class ImportCompleteEvent extends ImportEvent {

    int rowsProcessed;
    int passCount;
    int failCount;
    int failedValidations;
    long time;

    private SpreadsheetFile spreadsheetFile;

    public ImportCompleteEvent(User user, SpreadsheetFile spreadsheetFile, int rowsProcessed, int passCount,
                               int failedValidations, int failCount, long time) {
        this.spreadsheetFile = spreadsheetFile;
        this.rowsProcessed = rowsProcessed;
        this.passCount = passCount;
        this.failedValidations = failedValidations;
        this.failCount = failCount;
        this.time = time;
    }

    public int getRowsProcessed() {
        return rowsProcessed;
    }

    public long getTime() {
        return time;
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

    public SpreadsheetFile getSpreadsheetFile() {
        return spreadsheetFile;
    }

    @Override
    public String toString() {
        return "ImportCompleteEvent{"
                + "rowsProcessed=" + rowsProcessed
                + ", passCount=" + passCount
                + ", failCount=" + failCount
                + ", failedValidations=" + failedValidations
                + ", spreadsheetFile=" + spreadsheetFile
                + '}';
    }
}
