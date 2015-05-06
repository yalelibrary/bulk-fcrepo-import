package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;

import java.io.IOException;
import java.util.List;

/**
 * Reads Excel spreadsheet. Subject to modification.
 * @author Osman Din
 */
public abstract class AbstractImportEngine implements ImportEngine {

    private Spreadsheet spreadsheet = null;

    protected Integer USER_ID = 0; //TODO

    protected Integer PROJECT_ID = 0; //TODO

    protected OaiProvider oaiProvider; //TODO

    public void setOaiProvider(OaiProvider oaiProvider) {
        this.oaiProvider = oaiProvider;
    }

    protected ImageFunctionProcessor imageFunctionProcessor; //TODO

    public void setImageFunctionProcessor(ImageFunctionProcessor imageFunctionProcessor) {
        this.imageFunctionProcessor = imageFunctionProcessor;
    }

    protected ImportSourceProcessor importSourceProcessor;

    public void setImportSourceProcessor(ImportSourceProcessor importSourceProcessor) {
        this.importSourceProcessor = importSourceProcessor;
    }

    /**
     * Read with default param settings.
     */
    public final List<Import.Row> read(Spreadsheet file) throws ImportReaderValidationException, IOException {
        return read(file, ReadMode.FULL);
    }

    /**
     * Reads a given spreadsheet and returns row entities.
     *
     * @param inputReadMode
     * @return list of row values. Perhaps should return sheet.
     */
    public final List<Import.Row> read(Spreadsheet file, ReadMode inputReadMode) throws ImportReaderValidationException, IOException {
        spreadsheet = file;
        List<Import.Row> rows = doRead(file, inputReadMode);
        return rows;
    }

    /**
     * Writes to tables.
     *
     * @param list
     */
    public final int write(List<Import.Row> list) {
        return doWrite(list);
    }

    /**
     * Writes to tables.
     *
     * @param list
     */
    public final int write(List<Import.Row> list, Spreadsheet spreadsheet, int requestId) {
        return doWrite(list, spreadsheet, requestId);
    }

    protected Spreadsheet getFile() {
        return spreadsheet.clone();
    }

    public abstract List<Import.Row> doRead(Spreadsheet file, ReadMode mode)
            throws ImportReaderValidationException, IOException;

    public abstract int doWrite(List<Import.Row> file);

    public abstract int doWrite(List<Import.Row> file, Spreadsheet spreadsheet, int requestId);

}