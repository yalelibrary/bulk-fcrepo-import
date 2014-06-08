package edu.yale.library.ladybird.engine.exports;


import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportJobCtx;

import java.io.IOException;
import java.util.List;


public interface ExportEngine {
    ImportJobCtx read();

    void write(List<ImportEntity.Row> list, String spreadSheetFilePath) throws IOException;
}
