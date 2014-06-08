package edu.yale.library.ladybird.engine.imports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.FieldMarcMappingBuilder;
import edu.yale.library.ladybird.entity.ImportSourceData;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportWriterTest {

    private Logger logger = LoggerFactory.getLogger(ImportWriterTest.class);

    @Test
    public void shoudEqualOAIFunction() {
        final FieldConstantRules fieldConstantRules = new FieldConstantRules();
        final FieldConstant f104 = FunctionConstants.F104;
        final ImportEntity.Column<String> column1 = new ImportEntity().new Column<>(f104, String.valueOf("2222"));

        assert (fieldConstantRules.isOAIFunction(column1));

        final FieldConstant f1 = FunctionConstants.F1;
        final ImportEntity.Column<String> column2 = new ImportEntity().new Column<>(f1, String.valueOf("2222"));

        assert (!fieldConstantRules.isOAIFunction(column2));
    }


    @Test
    public void shouldContainBibIdMarcTags() {
        final ImportWriter importWriter = new ImportWriter();
        final PropUtil util = new PropUtil();
        final OaiProvider provider = new OaiProvider("id",
                util.getProperty("oai_test_url_prefix"),
                util.getProperty("oai_url_id"));
        importWriter.setOaiProvider(provider);
        final String bibId = "9807234";
        final List<String> bibIds = Collections.singletonList(bibId);

        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();

        final Map<String, Multimap<Marc21Field, ImportSourceData>> map =
                importSourceDataReader.readBibIdMarcData(provider, bibIds, null, 0); //FIXME params null and 0 for importid
        assertEquals("Map size mismatch", map.size(), 1);
        final Multimap<Marc21Field, ImportSourceData> innerMap = map.get(bibId);
        Collection<ImportSourceData> collection = innerMap.get(Marc21Field._520);
        final Iterator<ImportSourceData> it = collection.iterator();

        String text = "";
        while (it.hasNext()) {
            ImportSourceData importSourceData = it.next();
            text = importSourceData.getValue();
        }

        assertTrue("Text missing", text.contains("by the sun, the moon, the stars"));
        //TODO Test other values/mappings here. . .
    }

    @Test
    public void shouldTranslateToMarc21Field() {
        final ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();
        final Marc21Field marc21Field = importSourceDataReader.getMar21FieldForString("245");
        assert (marc21Field.equals(Marc21Field._245));
    }

   @Test
   public void shouldBuildMarcFdidMap() {
       final List<FieldMarcMapping> fieldMarcMappingList = new ArrayList<>();

       //Add some legit and non legit field marc mappings:
       final FieldMarcMapping fieldMarcMapping1 = new FieldMarcMappingBuilder()
               .setK1("245").setK2("a").setDate(new Date()).setFdid(69).createFieldMarcMapping();
       fieldMarcMappingList.add(fieldMarcMapping1);

       final FieldMarcMapping fieldMarcMapping2 = new FieldMarcMappingBuilder()
               .setK1("520").setK2("a").setDate(new Date()).setFdid(69).createFieldMarcMapping();
       fieldMarcMappingList.add(fieldMarcMapping2);

       final FieldMarcMapping fieldMarcMapping3 = new FieldMarcMappingBuilder()
               .setK1("999").setK2("a").setDate(new Date()).setFdid(69).createFieldMarcMapping();
       fieldMarcMappingList.add(fieldMarcMapping2);

       ImportWriter importWriter = new ImportWriter();
       Map<Marc21Field, FieldMarcMapping> marc21FieldMap = importWriter.buildMarcFdidMap(fieldMarcMappingList);
       //logger.debug(marc21FieldMap.toString());

       assert (marc21FieldMap.get(Marc21Field._245) == fieldMarcMapping1);
       assert (marc21FieldMap.get(Marc21Field._520) == fieldMarcMapping2);

       assert (marc21FieldMap.size() == 2);
    }

    /**
     * General utility. Subject to removal.
     */
    public class PropUtil {
        final Properties prop;

        {
            prop = new Properties();
            InputStream input = null;

            try {
                input = PropUtil.class.getResourceAsStream("/ladybird.properties");
                prop.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public String getProperty(String p) {
            return prop.getProperty(p);
        }
    }

}
