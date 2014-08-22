package edu.yale.library.ladybird.engine.imports;

import org.apache.commons.lang3.exception.ContextedRuntimeException;

/**
 *  Reprsents a runtime exception corresponding to an import job
 */
public class JobExceptionEvent extends edu.yale.library.ladybird.kernel.events.imports.ImportEvent {

    /** import id */
    private Integer jobId;
    private ContextedRuntimeException exception;

    public JobExceptionEvent(Integer jobId, ContextedRuntimeException exception) {
        this.jobId = jobId;
        this.exception = exception;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
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
                + "jobId=" + jobId
                + ", exception=" + exception
                + '}';
    }
}
