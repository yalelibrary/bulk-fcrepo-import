package edu.yale.library.ladybird.engine.model;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.ImportSourceData;

/**
 * Represents LocalIdentifier Marc21Field ImportSourceData values
 */
public class LocalIdMarcImportSource {

    private LocalIdentifier<String> bibId;

    private Multimap<Marc21Field, ImportSourceData> valueMap;

    public LocalIdentifier<String> getBibId() {
        return bibId;
    }

    public void setBibId(LocalIdentifier<String> bibId) {
        this.bibId = bibId;
    }

    public Multimap<Marc21Field, ImportSourceData> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Multimap<Marc21Field, ImportSourceData> value) {
        this.valueMap = value;
    }
}
