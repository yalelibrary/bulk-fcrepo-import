package edu.yale.library.ladybird.engine.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class LocalIdMarcValueTest {

    @Test
    public void shouldBuildMap() {
        LocalIdMarcValue localIdMarcValue = new LocalIdMarcValue();
        localIdMarcValue.setBibId(new LocalIdentifier("980725"));

        Map<String, String> firstSetFor245 = new HashMap();
        firstSetFor245.put("a", "a field 1");
        firstSetFor245.put("b", "b field 1");
        firstSetFor245.put("6", "6 field 1");

        Map<String, String> secondSetFor245 = new HashMap();
        secondSetFor245.put("a", "a field 2");
        secondSetFor245.put("b", "b field 2");
        secondSetFor245.put("6", "6 field 2");

        Multimap<Marc21Field, Map<String, String>> multiMap = HashMultimap.create();
        multiMap.put(Marc21Field._245, firstSetFor245);
        multiMap.put(Marc21Field._245, secondSetFor245);

        localIdMarcValue.setValueMap(multiMap);

        //test:
        assertEquals("Value mismatch", localIdMarcValue.getBibId().getId(), "980725");

        Multimap<Marc21Field, Map<String, String>> multiMapReadback = localIdMarcValue.getValueMap();
        Collection collection = multiMapReadback.get(Marc21Field._245);

        assertEquals(collection.size(), 2);

        Iterator it = collection.iterator();

        Map<String, String> first = (Map) it.next();

        assert (first.get("a").toString().equals("a field 1"));
        assert (first.get("b").toString().equals("b field 1"));
        assert (first.get("6").toString().equals("6 field 1"));

        Map<String, String> second = (Map) it.next();

        assert (second.get("a").toString().equals("a field 2"));
        assert (second.get("b").toString().equals("b field 2"));
        assert (second.get("6").toString().equals("6 field 2"));
    }
}
