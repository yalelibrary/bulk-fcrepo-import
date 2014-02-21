package edu.yale.library.engine.imports;


import edu.yale.library.engine.model.ImportReaderValidationException;
import edu.yale.library.engine.model.DefaultFieldDataValidator;
import edu.yale.library.engine.model.ReadMode;

import java.io.IOException;
import java.util.List;

/**
 * Implementations provide functionality specific to import. Subject to name change.
 */
public interface ImportEngine
{
    List<ImportEntity.Row> read(SpreadsheetFile file) throws ImportReaderValidationException, IOException;

    List<ImportEntity.Row> read(SpreadsheetFile file, ReadMode inputReadMode, DefaultFieldDataValidator validator)
            throws ImportReaderValidationException, IOException;

    int write(List<ImportEntity.Row> list);

}
