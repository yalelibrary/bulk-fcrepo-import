package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;

public class ExportCompleteEventBuilder {
    private User user;
    private SpreadsheetFile spreadsheetFile;
    private int rowsProcessed;
    private int passCount;
    private int failedValidations;
    private int failCount;

    public ExportCompleteEventBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public ExportCompleteEventBuilder setSpreadsheetFile(SpreadsheetFile spreadsheetFile) {
        this.spreadsheetFile = spreadsheetFile;
        return this;
    }

    public ExportCompleteEventBuilder setRowsProcessed(int rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
        return this;
    }

    public ExportCompleteEventBuilder setPassCount(int passCount) {
        this.passCount = passCount;
        return this;
    }

    public ExportCompleteEventBuilder setFailedValidations(int failedValidations) {
        this.failedValidations = failedValidations;
        return this;
    }

    public ExportCompleteEventBuilder setFailCount(int failCount) {
        this.failCount = failCount;
        return this;
    }

    public ExportCompleteEvent createExportCompleteEvent() {
        return new ExportCompleteEvent(user, spreadsheetFile, rowsProcessed, passCount, failedValidations, failCount);
    }
}