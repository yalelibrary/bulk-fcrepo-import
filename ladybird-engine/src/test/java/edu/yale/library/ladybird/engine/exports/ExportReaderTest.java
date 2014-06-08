package edu.yale.library.ladybird.engine.exports;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.imports.ImportSourceDataReader;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.entity.FieldDefinitionBuilder;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.entity.ImportSourceDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ExportReaderTest {

    private Logger logger = LoggerFactory.getLogger(ExportReaderTest.class);

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

    //This method would change as marc fdids bindings or what to return for a binding are changed.
    /** @see edu.yale.library.ladybird.engine.model.FieldConstantRules#getFieldConstantToMarc21Mapping for specific bindings  */
    @Test
    public void shouldEqualMarc21Mapping() {

        final FieldConstant f1 = new FieldDefinition(70, "A FIELD");
        final Marc21Field marc21Field1 = FieldConstantRules.getFieldConstantToMarc21Mapping(f1);
        assertEquals("Marc21 field mismatch", marc21Field1, Marc21Field._245);

        final FieldConstant f2 = new FieldDefinition(69, "SOME FIELD");
        final Marc21Field marc21Field2 = FieldConstantRules.getFieldConstantToMarc21Mapping(f2);
        assertEquals("Marc21 field mismatch", marc21Field2, Marc21Field.UNK);
    }



    /** @see edu.yale.library.ladybird.engine.exports.ExportReader#getMultiMapValue */
    @Test
    public void shouldGetMultiMapValue() {
        ExportReader exportReader = new ExportReader();
        final List<FieldConstant> globalFConstantsList = fakeGlobalApplicationFieldConstants();

        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();
        final Multimap<Marc21Field, Map<String, String>> map = importSourceDataReader.marshallMarcData(fakeImportSourceData());

        //logger.debug("Map={}", map.toString());

        for (FieldConstant f: globalFConstantsList) {
            String s = exportReader.getMultiMapValue(f, map);
            //logger.debug("Value={}", s);
            assertEquals("Value mismatch", s, "Test value");
        }
    }

    private List<ImportSourceData> fakeImportSourceData() {
        ImportSourceData importSourceData = new ImportSourceDataBuilder().setK1("245").setK2("a").setAttr("a").setValue("Test value").setAttrVal("a").createImportSourceData();
        List<ImportSourceData> lsit = new ArrayList<>();
        lsit.add(importSourceData);
        return lsit;
    }

    private List<FieldConstant> fakeGlobalApplicationFieldConstants() {
        final List<FieldConstant> fieldConstants = new ArrayList<>();
        final FieldDefinition fieldDefintion = new FieldDefinitionBuilder()
                .setFdid(70).setAcid(105457).setDate(new Date()).setHandle("Fake field").createFieldDefinition();
        fieldConstants.add(fieldDefintion);
        return fieldConstants;
    }

    private Multimap fakeMultipmap() {
        Multimap<Marc21Field, Map<String, String>> map = HashMultimap.create();
        Map<String, String> mapString = new HashMap();
        map.put(Marc21Field._245, mapString);
        return map;
    }

}
