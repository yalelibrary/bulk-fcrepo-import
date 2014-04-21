package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.exports.ExportReader;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.Marc21Field;
import edu.yale.library.ladybird.engine.model.FunctionConstantsRules;
import org.junit.Test;

import java.util.*;

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

        final List<FieldConstant> fieldConstantList2 = FunctionConstantsRules.getApplicationFieldConstants();

        assertEquals("FieldConstants list size mismatch", fieldConstantList2.size(),
                FunctionConstants.values().length + 1);
    }

    @Test
    public void shouldConvertStringToFieldConst() {
        final ExportReader exportReader = new ExportReader();
        final FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
        final Map<String, FieldConstant> fdidMap = new HashMap<>();
        fdidMap.put("70", new FieldDefinitionValue(70, "fdid=70"));
        fieldDefinitionValue.setFieldDefMap(fdidMap);

        final FieldConstant f = exportReader.convertStringToFieldConstant("70");
        assertEquals("Value mismatch", f.getName(), "fdid=70");
    }

    @Test
    public void shouldEqualMarc21Mapping() {
        final ExportReader exportReader = new ExportReader();
        final FieldConstant f = new FieldDefinitionValue(70, "70");
        final Marc21Field marc21Field = exportReader.getFieldConstantToMarc21Mapping(f);
        assertEquals("Marc21 field mismatch", marc21Field, Marc21Field._245);

        final FieldConstant f2 = new FieldDefinitionValue(70, "fdid=70");
        final Marc21Field marc21Field2 = exportReader.getFieldConstantToMarc21Mapping(f2);
        assertEquals("Marc21 field mismatch", marc21Field2, Marc21Field.UNK);
    }

}
