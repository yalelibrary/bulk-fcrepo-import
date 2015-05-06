package edu.yale.library.ladybird.engine.imports;


/**
 * A checked exception used in case halting spreadsheet read is desired in case of validation error
 *
 * @author Osman Din
 * @see ReadMode
 */
public class ImportReaderValidationException extends Exception {

    public ImportReaderValidationException(Throwable cause) {
        super(cause);
    }

}
