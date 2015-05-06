package edu.yale.library.ladybird.engine.imports;


import org.apache.commons.lang3.exception.ContextedRuntimeException;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportEngineException extends ContextedRuntimeException {

    public ImportEngineException(Throwable cause) {
        super(cause);
    }

}
