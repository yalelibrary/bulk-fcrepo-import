package edu.yale.library.ladybird.engine.exports;


import edu.yale.library.ladybird.engine.imports.ImportEntity;

import java.io.IOException;
import java.util.List;

/**
 * Reads Excel spreadsheet. Subject to modification.
 */
public abstract class AbstractExportEngine implements ExportEngine {

    protected static final Integer USER_ID = 0; //FIXME

    /**
     * Reads import job tables and returns row entities.
     *
     * @return list of row values and job context
     */
    public ImportEntityContext read() {
        final ImportEntityContext importEntityContext = doRead();
        return importEntityContext;
    }

    /**
     * Writes to spreadsheet (etc).
     *
     * @param list
     */
    public final void write(final List<ImportEntity.Row> list, final String pathName) throws IOException {
        doWrite(list, pathName);
    }


    /**
     * Writes to spreadsheet (etc) using multiple sheets.
     *
     * @param list
     */
    public final void writeSheets(final List<ExportSheet> list, final String pathName) throws IOException {
        doWriteSheets(list, pathName);
    }

    public abstract ImportEntityContext doRead();

    public abstract void doWrite(List<ImportEntity.Row> file, String pathName) throws IOException;

    public abstract void doWriteSheets(List<ExportSheet> file, String pathName) throws IOException;

}