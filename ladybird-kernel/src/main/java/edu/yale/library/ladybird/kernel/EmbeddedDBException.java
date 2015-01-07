package edu.yale.library.ladybird.kernel;

public class EmbeddedDBException extends RuntimeException {

    public EmbeddedDBException(Throwable cause) {
        super(cause);
    }

    public EmbeddedDBException(String message) {
        super(message);
    }
}
