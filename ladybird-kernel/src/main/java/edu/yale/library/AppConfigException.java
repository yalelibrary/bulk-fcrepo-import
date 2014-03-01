package edu.yale.library;

/**
 * Mostly a wrapper for uninteresting checked exceptions
 */
public class AppConfigException extends RuntimeException {
    public AppConfigException(Throwable cause) {
        super(cause);
    }

    public AppConfigException(String message) {
        super(message);
    }
}
