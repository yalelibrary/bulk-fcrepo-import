package edu.yale.library.engine.exports;


import edu.yale.library.engine.imports.ImportEntity;
import edu.yale.library.engine.model.UnknownFunctionException;

import java.io.IOException;
import java.util.List;

/**
 * Reads Excel spreadsheet. Subject to modification.
 */
public abstract class AbstractExportEngine implements ExportEngine
{

    protected final static Integer USER_ID = 0; //FIXME

    /**
     * Reads import job tables and returns row entities.
     *
     * @return list of row values. Perhaps should return sheet.
     */
    public final List<ImportEntity.Row> read()
    {
        List<ImportEntity.Row> rows = doRead();
        return rows;
    }

    /**
     * Writes to spreadsheet (etc).
     * @param list
     */
    public final void write(final List<ImportEntity.Row> list, final String pathName) throws IOException
    {
        doWrite(list, pathName);
    }

    public abstract List<ImportEntity.Row> doRead();

    public abstract void doWrite(List<ImportEntity.Row> file, String pathName) throws IOException;


}


 
