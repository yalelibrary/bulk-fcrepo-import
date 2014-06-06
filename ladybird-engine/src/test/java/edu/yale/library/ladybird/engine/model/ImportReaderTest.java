package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportReader;
import edu.yale.library.ladybird.entity.FieldConstant;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        } catch (UnknownFieldConstantException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldReturnTrueForFunctionConstant() {
        assertTrue(FunctionConstants.isFunction("F1"));
        assertFalse(FunctionConstants.isFunction("F-X"));
    }
}
