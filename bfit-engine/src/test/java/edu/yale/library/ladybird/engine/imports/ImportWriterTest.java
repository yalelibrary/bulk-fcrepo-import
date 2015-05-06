package edu.yale.library.ladybird.engine.imports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.Util;
import edu.yale.library.ladybird.engine.metadata.FieldConstantUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import edu.yale.library.ladybird.engine.oai.FdidMarcMappingUtil;
import edu.yale.library.ladybird.engine.oai.ImportSourceDataReader;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.FieldMarcMappingBuilder;
import edu.yale.library.ladybird.entity.ImportSourceData;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportWriterTest {

    @Test
    public void shoudEqualOAIFunction() {
        final FieldConstantUtil fieldConstantUtil = new FieldConstantUtil();
        final FieldConstant f104 = FunctionConstants.F104;
        final Import.Column<String> column1 = new Import().new Column<>(f104, String.valueOf("2222"));

        assert (fieldConstantUtil.isOAIFunction(column1));

        final FieldConstant f1 = FunctionConstants.F1;
        final Import.Column<String> column2 = new Import().new Column<>(f1, String.valueOf("2222"));

        assert (!fieldConstantUtil.isOAIFunction(column2));
    }

    @Test
    public void shouldContainBibIdMarcTags() throws Exception {
        final ImportWriter importWriter = new ImportWriter();
        final Util util = new Util();
        final OaiProvider provider = new OaiProvider("id",
                util.getProperty("oai_test_url_prefix"),
                util.getProperty("oai_url_id"));
        importWriter.setOaiProvider(provider);
        final String bibId = "9807234";
        final LocalIdentifier<String> localIdentifier = new LocalIdentifier<String>(bibId);
        final List<LocalIdentifier<String>> bibIds = Collections.singletonList(localIdentifier);

        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();

        final List<LocalIdMarcImportSource> list =
                importSourceDataReader.readMarc(provider, bibIds, 0); //FIXME params 0 for importid
        assertEquals("List size mismatch", list.size(), 1);
        final Multimap<Marc21Field, ImportSourceData> innerMap = list.get(0).getValueMap();
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
        final Marc21Field marc21Field = Marc21Field.getMar21FieldForString("245");
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
       fieldMarcMappingList.add(fieldMarcMapping3);

       Map<Marc21Field, FieldMarcMapping> marc21FieldMap
               = new FdidMarcMappingUtil().buildMarcFdidMap(fieldMarcMappingList);

       assert (marc21FieldMap.get(Marc21Field._245) == fieldMarcMapping1);
       assert (marc21FieldMap.get(Marc21Field._520) == fieldMarcMapping2);

       assert (marc21FieldMap.size() == 2);
    }

}
