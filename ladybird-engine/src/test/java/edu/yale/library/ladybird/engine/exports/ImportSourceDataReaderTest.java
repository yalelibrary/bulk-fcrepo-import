package edu.yale.library.ladybird.engine.exports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.imports.ImportSourceDataReader;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.entity.ImportSourceDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 *
 */
public class ImportSourceDataReaderTest {

    private Logger logger = LoggerFactory.getLogger(ImportSourceDataReaderTest.class);
    /**
     *  @see edu.yale.library.ladybird.engine.imports.ImportSourceDataReader#marshallMarcData(java.util.List)
     *  <code>
        public Multimap<Marc21Field, Map<String, String>> marshallMarcData(final List<ImportSourceData> importSourceDataList) {
         logger.debug("Marshalling marc data from import source data.");

        //final Map<Marc21Field, DatafieldType> marcTagData = new HashMap<>();

        //populate map (e.g. (245,{a,"text"}), (245, {b,"text b"}). This map will then be read.
        final Multimap<Marc21Field, Map<String, String>> map = HashMultimap.create(); //k=tag, v={subfield,value}

         for (int i = 0; i < importSourceDataList.size(); i++) {
            final ImportSourceData entry = importSourceDataList.get(i);
            final String k1 = entry.getK1();

             switch (k1) {
                case "880":
                    //logger.debug("Ignoring field 880");
                    break;
                default:
                    logger.debug("Putting field={} value={}", k1, entry.getValue());
                    final Map<String, String> attrValue = new HashMap<>();
                    attrValue.put(entry.getK2(), entry.getValue());
                    map.put(Marc21Field.valueOfTag(k1), attrValue);
                    break;
            }
         }
        return map;
    </code>
    }
    */
    @Test
    public void shouldBuildMap() {
        final List<ImportSourceData> sourceData = fakeImportSourceData();
        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();

        Multimap<Marc21Field, Map<String, String>> m = importSourceDataReader.marshallMarcData(sourceData);
        logger.debug(m.toString());
        Collection marcMap = m.get(Marc21Field._245);

        Iterator it = marcMap.iterator();

        Map<String, String> map = (Map) it.next();
        assertEquals("Value mismatch", map.get("a"), "Test value");
    }

    private List<ImportSourceData> fakeImportSourceData() {
        ImportSourceData importSourceData = new ImportSourceDataBuilder().setK1("245").setK2("a").setAttr("test attr").setValue("Test value").setAttrVal("Test attr value").createImportSourceData();
        List<ImportSourceData> lsit = new ArrayList<>();
        lsit.add(importSourceData);
        return lsit;
    }
}
