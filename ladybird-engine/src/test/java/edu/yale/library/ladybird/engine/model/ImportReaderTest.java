package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ImportReaderTest {

    /**
     * @see edu.yale.library.ladybird.engine.model.FunctionConstants
     */
    @Test
    public void shouldEqualFuncContant() {
        try {
            final FieldConstant f = ImportReader.getFieldConstant("f104");
            assertEquals("Function constant name mismatch", f.getName(), "F104");
        } catch (UnknownFunctionException e) {
            e.printStackTrace();
        }
    }
}
