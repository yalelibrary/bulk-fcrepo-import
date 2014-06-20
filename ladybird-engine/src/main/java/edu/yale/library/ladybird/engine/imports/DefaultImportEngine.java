package edu.yale.library.ladybird.engine.imports;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultImportEngine extends AbstractImportEngine {
    private final Logger logger = getLogger(this.getClass());

    private static final Integer DEFAULT_SHEET = 0; //TODO

    @Override
    public List<ImportEntity.Row> doRead(final SpreadsheetFile file, final ReadMode readMode)
            throws ImportReaderValidationException, IOException {
        logger.debug("Initiating read={}", file.getAltName());

        ImportReader reader = new ImportReader(file, DEFAULT_SHEET, readMode);
        return reader.read();
    }

    @Override
    public int doWrite(final List<ImportEntity.Row> list) {
        logger.debug("Initiating write, userId={} projectId={} list size={}", USER_ID, PROJECT_ID, list.size());

        ImportWriter importWriter = new ImportWriter();
        importWriter.setOaiProvider(oaiProvider);  //TODO
        importWriter.setMediaFunctionProcessor(mediaFunctionProcessor); //TODO
        importWriter.setImportSourceProcessor(importSourceProcessor); //TODO

        ImportEntityValue importEntityValue = new ImportEntityValue(list);
        try {
            return importWriter.write(importEntityValue,
                    new ImportJobRequestBuilder().userId(USER_ID).file("").dir("").projectId(PROJECT_ID).build());
        } catch (Exception e) {
            throw new ImportEngineException(e);
        }
    }

    public DefaultImportEngine(int userId, int projectId) {
        super.USER_ID = userId;
        super.PROJECT_ID = projectId;
    }
}
