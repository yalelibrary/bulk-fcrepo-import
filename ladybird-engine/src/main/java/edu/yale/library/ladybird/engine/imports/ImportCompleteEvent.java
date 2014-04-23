package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.kernel.model.User;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;

/**
 * TODO field access. Subject to modification.
 */
public final class ImportCompleteEvent extends ImportEvent {

    int rowsProcessed;
    int passCount;
    int failCount;
    int failedValidations;

    private SpreadsheetFile spreadsheetFile;

    public ImportCompleteEvent(User user, SpreadsheetFile spreadsheetFile, int rowsProcessed, int passCount, int failedValidations, int failCount) {
        this.spreadsheetFile = spreadsheetFile;
        this.rowsProcessed = rowsProcessed;
        this.passCount = passCount;
        this.failedValidations = failedValidations;
        this.failCount = failCount;
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
