package edu.yale.library.engine.exports;


import edu.yale.library.engine.imports.ImportEntity;

import java.io.IOException;
import java.util.List;


public interface ExportEngine {
    List<ImportEntity.Row> read();

    void write(List<ImportEntity.Row> list, String spreadSheetFilePath) throws IOException;
}
