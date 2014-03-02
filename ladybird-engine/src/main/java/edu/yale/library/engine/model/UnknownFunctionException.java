package edu.yale.library.engine.model;

/**
 * An exception to represent unrecongized F or Fdid value.
 *
 * @see ImportReaderValidationException
 */
public class UnknownFunctionException extends Exception {
    public UnknownFunctionException() {
    }

    public UnknownFunctionException(String message) {
        super(message);
    }

    public UnknownFunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownFunctionException(Throwable cause) {
        super(cause);
    }

}
