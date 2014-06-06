package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportReader;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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


    /**
     * @see edu.yale.library.ladybird.engine.model.FunctionConstants
     */
    @Test
    public void shouldEqualFdid() {
        try {
            final FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
            final Map<String, FieldConstant> fdidMap = new HashMap<>();

            fdidMap.put("70", new FieldDefinitionValue(70, "fdid=70"));
            fieldDefinitionValue.setFieldDefMap(fdidMap);

            final FieldConstant f = ImportReader.getFieldConstant("70");
            assertEquals("Function constant name mismatch", f.getName(), "fdid=70");
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
