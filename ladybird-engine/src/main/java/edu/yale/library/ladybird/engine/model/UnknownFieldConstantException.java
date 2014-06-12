package edu.yale.library.ladybird.engine.model;

/**
 * An exception to represent unrecongized F or Fdid value.
 *
 * @see edu.yale.library.ladybird.engine.imports.ImportReaderValidationException
 */
public class UnknownFieldConstantException extends Exception {

    public UnknownFieldConstantException(String message) {
        super(message);
    }
}
