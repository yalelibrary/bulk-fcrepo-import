package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;

import java.io.IOException;
import java.util.List;

/**
 * Implementations provide functionality specific to import. Subject to name change.
 */
public interface ImportEngine {
    List<Import.Row> read(SpreadsheetFile file) throws ImportReaderValidationException, IOException;

    List<Import.Row> read(SpreadsheetFile file, ReadMode inputReadMode, DefaultFieldDataValidator validator)
            throws ImportReaderValidationException, IOException;

    int write(List<Import.Row> list);

    int write(List<Import.Row> list, SpreadsheetFile spreadsheetFile, int requestId);

    void setOaiProvider(OaiProvider oaiProvider);

    void setMediaFunctionProcessor(MediaFunctionProcessor mediaFunctionProcessor);

    void setImportSourceProcessor(ImportSourceProcessor importSourceProcessor);

}
