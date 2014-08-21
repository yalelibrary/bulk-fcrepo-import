package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.imports.JobExceptionEvent;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import edu.yale.library.ladybird.kernel.events.imports.ImportEvent;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportSourceProcessor {
    private Logger logger = LoggerFactory.getLogger(ImportSourceProcessor.class);

    final ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader(); //TODO

    public void process(final int importId, final OaiProvider oaiProvider, final ImportEntityValue importEntityValue) throws IOException, MarcReadingException {
        try {
            final List<LocalIdentifier<String>> bibIdList = LocalIdentifier.getLocalIdList(importEntityValue);
            final List<LocalIdMarcImportSource> importSrcLocalIds =
                    importSourceDataReader.readMarc(oaiProvider, bibIdList, importId);

            //post runtime marc related exceptions, since they are not caught up in the chain
            List<ContextedRuntimeException> exceptions = new ArrayList<>();

            //TODO add exception posting in readMarc if real time updates are needed
            for (LocalIdMarcImportSource localIdMarcImportSource: importSrcLocalIds) {
                if (localIdMarcImportSource.getException() != null) {
                    exceptions.add(localIdMarcImportSource.getException());
                    logger.trace("Added exception={}", localIdMarcImportSource.getException());
                }
            }

            final ImportEvent failedEvent = new JobExceptionEvent(importId, exceptions);
            ExportBus.postEvent(failedEvent);

            ImportSourceDataWriter importSourceDataWriter = new ImportSourceDataWriter();
            importSourceDataWriter.write(importSrcLocalIds, importId);
        } catch (ContextedRuntimeException e) {
            logger.error("Error import source processing.");
            throw e;
        }
    }
}
