package edu.yale.library.engine.model;


/**
 * A checked exception used in case hating spreadsheet read is desired in case of validation error
 * @see ReadMode
 */
public class ImportReaderValidationException extends Exception
{
    public ImportReaderValidationException()
    {
    }

    public ImportReaderValidationException(String message)
    {
        super(message);
    }

    public ImportReaderValidationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ImportReaderValidationException(Throwable cause)
    {
        super(cause);
    }

    public ImportReaderValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
