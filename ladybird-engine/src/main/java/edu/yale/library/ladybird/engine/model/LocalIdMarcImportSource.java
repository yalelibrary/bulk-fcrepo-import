package edu.yale.library.ladybird.engine.model;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.ImportSourceData;

/**
 * TOOD test
 */
public class LocalIdMarcImportSource {

    LocalIdentifier<String> bibId;

    Multimap<Marc21Field, ImportSourceData> value;

    public LocalIdentifier<String> getBibId() {
        return bibId;
    }

    public void setBibId(LocalIdentifier<String> bibId) {
        this.bibId = bibId;
    }

    public Multimap<Marc21Field, ImportSourceData> getValue() {
        return value;
    }

    public void setValue(Multimap<Marc21Field, ImportSourceData> value) {
        this.value = value;
    }
}
