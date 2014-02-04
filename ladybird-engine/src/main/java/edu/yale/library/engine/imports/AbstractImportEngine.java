package edu.yale.library.engine.imports;


import edu.yale.library.engine.model.UnknownFunctionException;
import edu.yale.library.engine.model.DefaultFieldDataValidator;
import edu.yale.library.engine.model.ReadMode;

import java.util.List;

/**
 * Reads Excel spreadsheet. Subject to modification.
 */
public abstract class AbstractImportEngine implements ImportEngine
{

    private SpreadsheetFile spreadsheetFile = null;

    protected final static Integer USER_ID = 0; //todo

    /**
     * Read with default param settings.
     * @see #read(SpreadsheetFile, edu.yale.library.engine.model.ReadMode, edu.yale.library.engine.model.DefaultFieldDataValidator)
     */
    public final List<ImportEntity.Row> read(SpreadsheetFile file) throws UnknownFunctionException
    {
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
           DefaultFieldDataValidator validator) throws UnknownFunctionException
    {
        spreadsheetFile = file;
        List<ImportEntity.Row> rows = doRead(file, inputReadMode);
        return rows;
    }

    /**
     * Writes to tables.
     * @param list
     */
    public final void write(List<ImportEntity.Row> list)
    {
        doWrite(list);
    }

    protected SpreadsheetFile getFile()
    {
        return spreadsheetFile.clone();
    }

    public abstract List<ImportEntity.Row> doRead(SpreadsheetFile file, ReadMode mode) throws UnknownFunctionException;

    public abstract void doWrite(List<ImportEntity.Row> file);


}


 
