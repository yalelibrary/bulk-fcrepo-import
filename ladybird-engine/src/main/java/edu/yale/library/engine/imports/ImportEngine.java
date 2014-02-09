package edu.yale.library.engine.imports;


import edu.yale.library.engine.model.UnknownFunctionException;
import edu.yale.library.engine.model.DefaultFieldDataValidator;
import edu.yale.library.engine.model.ReadMode;

import java.util.List;

/**
 * Implementations provide functionality specific to import. Subject to name change.
 */
public interface ImportEngine
{
    List<ImportEntity.Row> read(SpreadsheetFile file) throws UnknownFunctionException;

    List<ImportEntity.Row> read(SpreadsheetFile file, ReadMode inputReadMode, DefaultFieldDataValidator validator)
            throws UnknownFunctionException;

    void write(List<ImportEntity.Row> list);

}
