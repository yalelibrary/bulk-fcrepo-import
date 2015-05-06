package edu.yale.library.ladybird.engine.exports;


import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.ImportContext;

import java.io.IOException;
import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ExportEngine {

    ImportContext read();

    void write(List<Import.Row> list, String spreadSheetFilePath) throws IOException;

    void writeSheets(List<ExportSheet> list, String spreadSheetFilePath) throws IOException;
}
