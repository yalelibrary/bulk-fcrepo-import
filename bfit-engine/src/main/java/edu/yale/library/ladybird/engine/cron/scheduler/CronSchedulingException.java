package edu.yale.library.ladybird.engine.cron.scheduler;


/**
 * Wrapper around check Quarz scheduler exception
 */
public class CronSchedulingException extends RuntimeException {

    public CronSchedulingException(Throwable cause) {
        super(cause);
    }

}
