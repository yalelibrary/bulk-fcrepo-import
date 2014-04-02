package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.model.ImportReaderValidationException;
import edu.yale.library.ladybird.engine.model.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.model.ReadMode;
import edu.yale.library.ladybird.engine.oai.OaiProvider;

import java.io.IOException;
import java.util.List;

/**
 * Reads Excel spreadsheet. Subject to modification.
 */
public abstract class AbstractImportEngine implements ImportEngine {

    private SpreadsheetFile spreadsheetFile = null;

    protected static final Integer USER_ID = 0; //todo

    protected OaiProvider oaiProvider; //TODO design

    public void setOaiProvider(OaiProvider oaiProvider) {
        this.oaiProvider = oaiProvider;
    }

    /**
     * Read with default param settings.
     *
     * @see #read(SpreadsheetFile, edu.yale.library.ladybird.engine.model.ReadMode, edu.yale.library.ladybird.engine.model.DefaultFieldDataValidator)
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

    protected SpreadsheetFile getFile() {
        return spreadsheetFile.clone();
    }

    public abstract List<ImportEntity.Row> doRead(SpreadsheetFile file, ReadMode mode)
            throws ImportReaderValidationException, IOException;

    public abstract int doWrite(List<ImportEntity.Row> file);


}