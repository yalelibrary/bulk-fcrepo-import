package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;

import java.io.IOException;
import java.util.List;

/**
 * Reads Excel spreadsheet. Subject to modification.
 */
public abstract class AbstractImportEngine implements ImportEngine {

    private SpreadsheetFile spreadsheetFile = null;

    protected Integer USER_ID = 0; //todo

    protected Integer PROJECT_ID = 0; //todo

    protected OaiProvider oaiProvider; //TODO design

    public void setOaiProvider(OaiProvider oaiProvider) {
        this.oaiProvider = oaiProvider;
    }

    protected MediaFunctionProcessor mediaFunctionProcessor; //TODO desgin

    public void setMediaFunctionProcessor(MediaFunctionProcessor mediaFunctionProcessor) {
        this.mediaFunctionProcessor = mediaFunctionProcessor;
    }

    protected ImportSourceProcessor importSourceProcessor;

    public void setImportSourceProcessor(ImportSourceProcessor importSourceProcessor) {
        this.importSourceProcessor = importSourceProcessor;
    }

    /**
     * Read with default param settings.
     *
     * @see #read(SpreadsheetFile, ReadMode, edu.yale.library.ladybird.engine.DefaultFieldDataValidator)
     */
    public final List<ImportEntity.Row> read(SpreadsheetFile file) throws ImportReaderValidationException, IOException {
        return read(file, ReadMode.FULL, new DefaultFieldDataValidator());
    }

    /**
     * Reads a given spreadsheet and returns row entities.
     *
     * @param inputReadMode
     * @param validator
     * @return list of row values. Perhaps should return sheet.
     */
    public final List<ImportEntity.Row> read(SpreadsheetFile file, ReadMode inputReadMode,
                                             DefaultFieldDataValidator validator) throws ImportReaderValidationException, IOException {
        spreadsheetFile = file;
        List<ImportEntity.Row> rows = doRead(file, inputReadMode);
        return rows;
    }

    /**
     * Writes to tables.
     *
     * @param list
     */
    public final int write(List<ImportEntity.Row> list) {
        return doWrite(list);
    }

    /**
     * Writes to tables.
     *
     * @param list
     */
    public final int write(List<ImportEntity.Row> list, SpreadsheetFile spreadsheetFile) {
        return doWrite(list, spreadsheetFile);
    }

    protected SpreadsheetFile getFile() {
        return spreadsheetFile.clone();
    }

    public abstract List<ImportEntity.Row> doRead(SpreadsheetFile file, ReadMode mode)
            throws ImportReaderValidationException, IOException;

    public abstract int doWrite(List<ImportEntity.Row> file);

    public abstract int doWrite(List<ImportEntity.Row> file, SpreadsheetFile spreadsheetFile);

}