package edu.yale.library.engine.imports;

import edu.yale.library.beans.User;

public class ImportCompleteEventBuilder {
    private User user;
    private SpreadsheetFile spreadsheetFile;
    private int rowsProcessed;
    private int passCount;
    private int failedValidations;
    private int failCount;

    public ImportCompleteEventBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public ImportCompleteEventBuilder setSpreadsheetFile(SpreadsheetFile spreadsheetFile) {
        this.spreadsheetFile = spreadsheetFile;
        return this;
    }

    public ImportCompleteEventBuilder setRowsProcessed(int rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
        return this;
    }

    public ImportCompleteEventBuilder setPassCount(int passCount) {
        this.passCount = passCount;
        return this;
    }

    public ImportCompleteEventBuilder setFailedValidations(int failedValidations) {
        this.failedValidations = failedValidations;
        return this;
    }

    public ImportCompleteEventBuilder setFailCount(int failCount) {
        this.failCount = failCount;
        return this;
    }

    public ImportCompleteEvent createImportDoneEvent() {
        return new ImportCompleteEvent(user, spreadsheetFile, rowsProcessed, passCount, failedValidations, failCount);
    }
}