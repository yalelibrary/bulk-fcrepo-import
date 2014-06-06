package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ExportReaderTest {

    //TODO move
    @Test
    public void shouldContainApplicationFunctions() {
        final FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
        final Map<String, FieldConstant> fdidMap = new HashMap<>();

        fdidMap.put("", new FieldDefinitionValue(70, "fdid=70"));
        fieldDefinitionValue.setFieldDefMap(fdidMap);

        final List<FieldConstant> fieldConstantList2 = FieldConstantRules.getApplicationFieldConstants();

        assertEquals("FieldConstants list size mismatch", fieldConstantList2.size(),
                FunctionConstants.values().length + 1);
    }

    @Test
    public void shouldConvertStringToFieldConst() {
        final FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
        final Map<String, FieldConstant> fdidMap = new HashMap<>();
        fdidMap.put("70", new FieldDefinitionValue(70, "fdid=70"));
        fieldDefinitionValue.setFieldDefMap(fdidMap);

        final FieldConstant f = FieldConstantRules.convertStringToFieldConstant("70");
        assertEquals("Value mismatch", f.getName(), "fdid=70");
    }

    @Test
    public void shouldConvertFunctionStringToFieldConst() {
        final FieldConstant f = FieldConstantRules.convertStringToFieldConstant("F1");
        assert (f != null);
        assertEquals("Value mismatch", f.getName(), "F1");

        try {
            final FunctionConstants f1  = FunctionConstants.valueOf("F1");
            assertEquals("Value mismatch", f1.getName(), "F1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldEqualMarc21Mapping() {
        final FieldConstant f = new FieldDefinitionValue(70, "70");
        final Marc21Field marc21Field = FieldConstantRules.getFieldConstantToMarc21Mapping(f);
        assertEquals("Marc21 field mismatch", marc21Field, Marc21Field._245);

        final FieldConstant f2 = new FieldDefinitionValue(70, "fdid=70");
        final Marc21Field marc21Field2 = FieldConstantRules.getFieldConstantToMarc21Mapping(f2);
        assertEquals("Marc21 field mismatch", marc21Field2, Marc21Field.UNK);
    }

}
