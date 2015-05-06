package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;

import java.io.IOException;
import java.util.List;

/**
 * Implementations provide functionality specific to import. Subject to name change.
 * @author Osman Din
 */
public interface ImportEngine {

    List<Import.Row> read(Spreadsheet file) throws ImportReaderValidationException, IOException;

    List<Import.Row> read(Spreadsheet file, ReadMode inputReadMode)
            throws ImportReaderValidationException, IOException;

    int write(List<Import.Row> list);

    int write(List<Import.Row> list, Spreadsheet spreadsheet, int requestId);

    void setOaiProvider(OaiProvider oaiProvider);

    void setImageFunctionProcessor(ImageFunctionProcessor imageFunctionProcessor);

    void setImportSourceProcessor(ImportSourceProcessor importSourceProcessor);

}
