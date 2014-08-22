package edu.yale.library.ladybird.engine.imports;

import org.apache.commons.lang3.exception.ContextedRuntimeException;

/**
 *  Reprsents a runtime exception corresponding to an import job
 */
public class JobExceptionEvent extends edu.yale.library.ladybird.kernel.events.imports.ImportEvent {

    private Integer importId;
    private ContextedRuntimeException exception;

    public JobExceptionEvent(Integer importId, ContextedRuntimeException exception) {
        this.importId = importId;
        this.exception = exception;
    }

    public Integer getImportId() {
        return importId;
    }

    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public ContextedRuntimeException getException() {
        return exception;
    }

    public void setException(ContextedRuntimeException exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "JobExceptionEvent{"
                + "importId=" + importId
                + ", exception=" + exception
                + '}';
    }
}
