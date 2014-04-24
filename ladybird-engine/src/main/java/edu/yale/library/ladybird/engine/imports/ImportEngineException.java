package edu.yale.library.ladybird.engine.imports;


/**
 * For now a RuntimeException.
 */
public class ImportEngineException extends RuntimeException {
    public ImportEngineException() {
    }

    public ImportEngineException(String message) {
        super(message);
    }

    public ImportEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportEngineException(Throwable cause) {
        super(cause);
    }

    public ImportEngineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
