package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.imports.JobExceptionEvent;


public interface JobExceptionEventPoster {
    void post(JobExceptionEvent event);
}
