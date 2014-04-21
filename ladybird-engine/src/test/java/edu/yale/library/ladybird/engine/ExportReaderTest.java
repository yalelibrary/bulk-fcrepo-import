package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.exports.ExportReader;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.Marc21Field;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ExportReaderTest {

    @Test
    public void shouldContainApplicationFunctions() {
        final ExportReader exportReader = new ExportReader();
        final List<FieldConstant> fieldConstantList = exportReader.getApplicationFieldConstants();
        final FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
        final Map<String, FieldConstant> fdidMap = new HashMap<>();

        fdidMap.put("", new FieldDefinitionValue(70, "fdid=70"));
        fieldDefinitionValue.setFieldDefMap(fdidMap);

        final List<FieldConstant> fieldConstantList2 = exportReader.getApplicationFieldConstants();

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
    public void shouldEqualColValueForFieldConstant() {
        final ExportReader exportReader = new ExportReader();
        final List<ImportEntity.Column> columnList = new ArrayList<>();
        final FieldConstant f = new FieldDefinitionValue(70, "fdid=70");
        final ImportEntity.Column column = new ImportEntity(). new Column<>(f, "Test Col Value");
        columnList.add(column);

        assertEquals("Value mismatch", "Test Col Value", exportReader.findColValueForThisFieldConstant(f, columnList));
    }

    public Marc21Field getFieldConstantToMarc21Mapping(final FieldConstant fieldConstant) {
        if (fieldConstant.getName().equals("70")) {
            return Marc21Field._245;
        }
        return Marc21Field.UNK;
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
