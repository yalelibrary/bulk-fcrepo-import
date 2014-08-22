package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.imports.JobExceptionEvent;

/**
 * TODO Subject to removal
 */
public interface JobExceptionEventPoster {
    void post(JobExceptionEvent event);
}
