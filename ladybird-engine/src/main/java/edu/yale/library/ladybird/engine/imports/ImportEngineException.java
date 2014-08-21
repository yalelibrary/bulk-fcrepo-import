package edu.yale.library.ladybird.engine.imports;


import org.apache.commons.lang3.exception.ContextedRuntimeException;

public class ImportEngineException extends ContextedRuntimeException {

    public ImportEngineException(Throwable cause) {
        super(cause);
    }
}
