package edu.yale.library.ladybird.engine;


/**
 * Wrapper around check Quarz scheduler exception
 */
public class CronSchedulingException extends RuntimeException {
    public CronSchedulingException() {
    }

    public CronSchedulingException(String message) {
        super(message);
    }

    public CronSchedulingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CronSchedulingException(Throwable cause) {
        super(cause);
    }

}
