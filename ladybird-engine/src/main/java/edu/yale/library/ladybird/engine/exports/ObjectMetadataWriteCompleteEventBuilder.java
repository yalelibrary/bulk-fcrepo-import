package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.Spreadsheet;
import edu.yale.library.ladybird.entity.User;

public class ObjectMetadataWriteCompleteEventBuilder {
    private User user;
    private Spreadsheet spreadsheet;
    private int rowsProcessed;
    private int passCount;
    private int failedValidations;
    private int failCount;
    private long time;

    public ObjectMetadataWriteCompleteEventBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public ObjectMetadataWriteCompleteEventBuilder setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
        return this;
    }

    public ObjectMetadataWriteCompleteEventBuilder setRowsProcessed(int rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
        return this;
    }

    public ObjectMetadataWriteCompleteEventBuilder setPassCount(int passCount) {
        this.passCount = passCount;
        return this;
    }

    public ObjectMetadataWriteCompleteEventBuilder setFailedValidations(int failedValidations) {
        this.failedValidations = failedValidations;
        return this;
    }

    public ObjectMetadataWriteCompleteEventBuilder setFailCount(int failCount) {
        this.failCount = failCount;
        return this;
    }

    public ObjectMetadataWriteCompleteEventBuilder setTime(long time) {
        this.time = time;
        return this;
    }

    public ObjectMetadataWriteCompleteEvent createObjectMetadataWriteCompleteEvent() {
        return new ObjectMetadataWriteCompleteEvent(user, spreadsheet, rowsProcessed, passCount, failedValidations, failCount, time);
    }
}