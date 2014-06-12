package edu.yale.library.ladybird.engine.imports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.oai.Record;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.entity.ImportSourceDataBuilder;
import org.junit.Test;

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

    @Test
    public void shouldBuildMap() {
        final List<ImportSourceData> sourceData = getImportSourceData();
        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();

        Multimap<Marc21Field, Map<String, String>> m = importSourceDataReader.buildMultimap(sourceData);

        //logger.debug(m.toString());

        Collection marcMap = m.get(Marc21Field._245);
        Iterator it = marcMap.iterator();

        Map<String, String> map = (Map) it.next();
        assertEquals("Value mismatch", map.get("a"), "Test value");
    }

    private List<ImportSourceData> getImportSourceData() {
        ImportSourceData importSourceData = new ImportSourceDataBuilder().setK1("245").setK2("a")
                .setAttr("test attr").setValue("Test value").setAttrVal("Test attr value").createImportSourceData();
        List<ImportSourceData> list = new ArrayList<>();
        list.add(importSourceData);

        ImportSourceData importSourceData2 = new ImportSourceDataBuilder().setK1("245").setK2("a")
                .setAttr("test attr 2").setValue("Test value 2").setAttrVal("Test attr value 2").createImportSourceData();
        list.add(importSourceData2);

        return list;
    }

    @Test
    public void shouldBuildMultiMap() { //TODO
        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();
        importSourceDataReader.buildMultiMap(new LocalIdentifier<>("1122"), new Record(), 0);
    }

    @Test
    public void shouldGetLocalIdMarcValueTest() { //TODO
        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();
        importSourceDataReader.readImportSourceData(0);
    }
}
