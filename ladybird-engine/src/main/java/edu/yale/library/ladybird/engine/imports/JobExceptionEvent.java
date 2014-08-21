package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.Monitor;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;

/**
 *  In memory representation of an import job execution exception
 *  TODO re-design
 */
public class JobExceptionEvent extends ImportEvent {

    private SpreadsheetFile spreadsheetFile;
    private Monitor monitor;
    private Exception exception;

    public SpreadsheetFile getSpreadsheetFile() {
        return spreadsheetFile;
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

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public JobExceptionEvent(SpreadsheetFile spreadsheetFile, Monitor monitor, Exception exception) {
        this.spreadsheetFile = spreadsheetFile;
        this.monitor = monitor;
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "JobExceptionEvent{"
                +"spreadsheetFile=" + spreadsheetFile
                +", monitor=" + monitor
                +'}';
    }
}
