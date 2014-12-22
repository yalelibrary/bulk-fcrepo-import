package edu.yale.library.ladybird.engine.exports;


import edu.yale.library.ladybird.engine.imports.Import;

import java.io.IOException;
import java.util.List;


public interface ExportEngine {
    ImportEntityContext read();

    void write(List<Import.Row> list, String spreadSheetFilePath) throws IOException;

    void writeSheets(List<ExportSheet> list, String spreadSheetFilePath) throws IOException;

}
