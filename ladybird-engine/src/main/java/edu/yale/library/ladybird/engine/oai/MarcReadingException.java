package edu.yale.library.ladybird.engine.oai;

/**
 * Generic MarcReadingException. Wraps underlying low-level exception
 */
public class MarcReadingException extends Exception {
    public MarcReadingException() {
    }

    public MarcReadingException(String message) {
        super(message);
    }

    public MarcReadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarcReadingException(Throwable cause) {
        super(cause);
    }

    public MarcReadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
