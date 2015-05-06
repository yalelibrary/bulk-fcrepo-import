package edu.yale.library.ladybird.engine.model;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Represents LocalIdentifier Marc21Field string values
 *
 * @author Osman Din
 */
public class LocalIdMarcValue {

    private static Logger logger = LoggerFactory.getLogger(LocalIdMarcValue.class);

    private LocalIdentifier<String> bibId;

    private Multimap<Marc21Field, Map<String, String>> valueMap;
    //e.g. {(500 -> {("a", "some value")}, 245 -> { ("a", "value"), ("b", "value 2") }, {("a", "value c")}}
    // repeating 245 field (if it doesn't repeat, get rid of the multi map).

    public LocalIdentifier<String> getBibId() {
        return bibId;
    }

    public void setBibId(LocalIdentifier<String> bibId) {
        this.bibId = bibId;
    }

    public Multimap<Marc21Field, Map<String, String>> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Multimap<Marc21Field, Map<String, String>> value) {
        this.valueMap = value;
    }

    /**
     * Find match in list
     * @param bibIdValueList list of LocalIdMarcValue
     * @param value LocalIdMarcValue or null
     * @return
     */
    public static LocalIdMarcValue findMatch(List<LocalIdMarcValue> bibIdValueList, String value) {

        for (LocalIdMarcValue localIdMarcValue : bibIdValueList) {
            logger.trace("Comparing bibIdcolum={} with value={}", value, localIdMarcValue.getBibId().getId());

            if (localIdMarcValue.getBibId().getId().equals(value)) {
                return localIdMarcValue;
            }
        }
        return null;
    }


}
