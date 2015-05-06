package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.imports.JobExceptionEvent;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface JobExceptionEventPoster {
    void post(JobExceptionEvent event);
}
