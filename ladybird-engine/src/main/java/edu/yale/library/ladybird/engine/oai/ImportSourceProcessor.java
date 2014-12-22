package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.engine.imports.ImportValue;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ImportSourceProcessor {
    private Logger logger = LoggerFactory.getLogger(ImportSourceProcessor.class);

    final ImportSourceDataReader reader = new ImportSourceDataReader(); //TODO
    final ImportSourceDataWriter writer = new ImportSourceDataWriter();

    /** Reaads and writes marc data */
    public void process(final int importId, final OaiProvider provider, final ImportValue importValue)
            throws IOException, MarcReadingException {
        try {
            final List<LocalIdentifier<String>> bibIdList = LocalIdentifier.getLocalIdList(importValue);
            final List<LocalIdMarcImportSource> list = reader.readMarc(provider, bibIdList, importId);
            writer.write(list, importId);
        } catch (ContextedRuntimeException e) {
            logger.error("Error import source processing.");
            throw e;
        }
    }
}
