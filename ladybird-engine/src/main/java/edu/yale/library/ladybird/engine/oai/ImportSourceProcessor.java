package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public class ImportSourceProcessor {
    private Logger logger = LoggerFactory.getLogger(ImportSourceProcessor.class);

    public void process(final int importId, final OaiProvider oaiProvider, final ImportEntityValue importEntityValue) throws IOException, MarcReadingException{
        try {
            final List<LocalIdentifier<String>> bibIdList = LocalIdentifier.getLocalIdList(importEntityValue);
            final ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();
            final List<LocalIdMarcImportSource> importSourceLocalIdList =
                    importSourceDataReader.readMarc(oaiProvider, bibIdList, importId);
            ImportSourceDataWriter importSourceDataWriter = new ImportSourceDataWriter();
            importSourceDataWriter.write(importSourceLocalIdList, importId);
        } catch (Exception e) {
            logger.error("Error import source processing.");
            throw e;
        }
    }
}
