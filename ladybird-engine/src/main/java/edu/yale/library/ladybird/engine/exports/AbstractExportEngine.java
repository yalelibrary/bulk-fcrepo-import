package edu.yale.library.ladybird.engine.exports;


import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportJobCtx;

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
    public ImportJobCtx read() {
        final ImportJobCtx importJobCtx = doRead();
        return importJobCtx;
    }

    /**
     * Writes to spreadsheet (etc).
     *
     * @param list
     */
    public final void write(final List<ImportEntity.Row> list, final String pathName) throws IOException {
        doWrite(list, pathName);
    }

    public abstract ImportJobCtx doRead();

    public abstract void doWrite(List<ImportEntity.Row> file, String pathName) throws IOException;

}