package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;
import org.apache.commons.lang3.exception.ContextedRuntimeException;

import java.util.List;

/**
 *  In memory representation of an import job execution exception
 *  TODO re-design
 */
public class JobExceptionEvent extends ImportEvent {

    private Integer importId;
    private List<ContextedRuntimeException> exception;

    public JobExceptionEvent(Integer importId, List<ContextedRuntimeException> exception) {
        this.importId = importId;
        this.exception = exception;
    }

    public Integer getImportId() {
        return importId;
    }

    public void setImportId(Integer importId) {
        this.importId = importId;
    }

    public List<ContextedRuntimeException> getException() {
        return exception;
    }

    public void setException(List<ContextedRuntimeException> exception) {
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
