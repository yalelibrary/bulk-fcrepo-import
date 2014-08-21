package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ImportSourceProcessor {
    private Logger logger = LoggerFactory.getLogger(ImportSourceProcessor.class);

    final ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader(); //TODO

    public void process(final int importId, final OaiProvider oaiProvider, final ImportEntityValue importEntityValue) throws IOException, MarcReadingException {
        try {
            final List<LocalIdentifier<String>> bibIdList = LocalIdentifier.getLocalIdList(importEntityValue);
            final List<LocalIdMarcImportSource> importSrcLocalIds =
                    importSourceDataReader.readMarc(oaiProvider, bibIdList, importId);

            ImportSourceDataWriter importSourceDataWriter = new ImportSourceDataWriter();
            importSourceDataWriter.write(importSrcLocalIds, importId);
        } catch (ContextedRuntimeException e) {
            logger.error("Error import source processing.");
            throw e;
        }
    }
}
