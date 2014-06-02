package edu.yale.library.ladybird.engine.imports;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultImportEngine extends AbstractImportEngine {
    private final Logger logger = getLogger(this.getClass());

    private static final Integer DEFAULT_SHEET = 0; //todo

    @Override
    public List<ImportEntity.Row> doRead(final SpreadsheetFile file, final ReadMode readMode)
            throws ImportReaderValidationException, IOException {
        logger.debug("Reading spreadsheet: " + file.getAltName());

        ImportReader reader = new ImportReader(file, DEFAULT_SHEET, readMode);
        return reader.processSheet();
    }

    @Override
    public int doWrite(final List<ImportEntity.Row> list) {
        logger.debug("Initiating write");

        ImportWriter importWriter = new ImportWriter();

        //TODO obtain file,dir,user //?

        importWriter.setOaiProvider(oaiProvider);  //TODO
        importWriter.setMediaFunctionProcessor(mediaFunctionProcessor); //TODO

        ImportEntityValue importEntityValue = new ImportEntityValue(list);
        return importWriter.write(importEntityValue,
                new ImportJobContextBuilder().userId(USER_ID).file("").dir("").build());
    }
}
