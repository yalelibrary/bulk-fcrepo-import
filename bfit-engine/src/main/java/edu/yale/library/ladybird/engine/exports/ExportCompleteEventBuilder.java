package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.Spreadsheet;
import edu.yale.library.ladybird.entity.User;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ExportCompleteEventBuilder {
    private User user;
    private Spreadsheet spreadsheet;
    private int rowsProcessed;
    private int passCount;
    private int failedValidations;
    private int failCount;
    private long time;

    public ExportCompleteEventBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public ExportCompleteEventBuilder setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
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

    public ExportCompleteEventBuilder setTime(long time) {
        this.time = time;
        return this;
    }

    public ExportCompleteEvent createExportCompleteEvent() {
        return new ExportCompleteEvent(user, spreadsheet, rowsProcessed, passCount, failedValidations, failCount, time);
    }
}