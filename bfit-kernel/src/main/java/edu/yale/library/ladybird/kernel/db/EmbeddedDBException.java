package edu.yale.library.ladybird.kernel.db;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class EmbeddedDBException extends RuntimeException {

    public EmbeddedDBException(Throwable cause) {
        super(cause);
    }

    public EmbeddedDBException(String message) {
        super(message);
    }
}
