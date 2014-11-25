package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.User;

public class ImportCompleteEventBuilder {
    private User user;
    private SpreadsheetFile spreadsheetFile;
    private int rowsProcessed;
    private int passCount;
    private int failedValidations;
    private int failCount;
    private long time;

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

    public ImportCompleteEventBuilder setTime(long time) {
        this.time = time;
        return this;
    }

    public ImportCompleteEvent createImportDoneEvent() {
        return new ImportCompleteEvent(user, spreadsheetFile, rowsProcessed, passCount, failedValidations, failCount, time);
    }
}