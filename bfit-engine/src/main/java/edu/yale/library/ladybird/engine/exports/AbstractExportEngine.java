package edu.yale.library.ladybird.engine.exports;


import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.ImportContext;

import java.io.IOException;
import java.util.List;

/**
 * Reads Excel spreadsheet. Subject to modification.
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public abstract class AbstractExportEngine implements ExportEngine {

    /**
     * Reads import job tables and returns row entities.
     *
     * @return list of row values and job context
     */
    public ImportContext read() {
        final ImportContext importContext = doRead();
        return importContext;
    }

    /**
     * Writes to spreadsheet (etc).
     *
     * @param list
     */
    public final void write(final List<Import.Row> list, final String pathName) throws IOException {
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

    public abstract ImportContext doRead();

    public abstract void doWrite(List<Import.Row> file, String pathName) throws IOException;

    public abstract void doWriteSheets(List<ExportSheet> file, String pathName) throws IOException;

}