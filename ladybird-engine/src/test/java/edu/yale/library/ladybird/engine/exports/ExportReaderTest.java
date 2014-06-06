package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.FieldDefinition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ExportReaderTest {

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
       // final FieldConstant f = new FieldDefinition(70, "70");
       // final Marc21Field marc21Field = FieldConstantRules.getFieldConstantToMarc21Mapping(f);
        //assertEquals("Marc21 field mismatch", marc21Field, Marc21Field._245);

        final FieldConstant f2 = new FieldDefinition(70, "fdid=70");
        final Marc21Field marc21Field2 = FieldConstantRules.getFieldConstantToMarc21Mapping(f2);
        assertEquals("Marc21 field mismatch", marc21Field2, Marc21Field.UNK);
    }

}
