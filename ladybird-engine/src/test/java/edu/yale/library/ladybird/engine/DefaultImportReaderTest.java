package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.imports.AbstractImportEngine;
import edu.yale.library.ladybird.engine.imports.DefaultImportEngine;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.SpreadsheetFile;
import edu.yale.library.ladybird.engine.model.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.model.ReadMode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class DefaultImportReaderTest {

    @Test
    public void execute() throws Exception {
        AbstractImportEngine abstractImportProcessor = new DefaultImportEngine();
        List<ImportEntity.Row> rows = abstractImportProcessor.read(getTestSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        Assert.assertEquals("Row size mismatch while reading spreadsheet", rows.size(), FileConstants.ROW_COUNT);
        assertEquals(rows.get(1).getColumns().get(4).getValue(), "Gilchrist, Scott");
    }

    public SpreadsheetFile getTestSpreadsheeet() throws IOException {
        SpreadsheetFile file = new SpreadsheetFile(FileConstants.TEST_XLS_FILE, "Test spreadsheet",
                FileConstants.TEST_XLS_FILE, getClass().getClassLoader().getResourceAsStream(FileConstants.TEST_XLS_FILE));

        return file;
    }


    public static class FileConstants {

        static final String TEST_XLS_FILE = "excel/4654-pt1-READY-FOR-INGEST-A.xlsx";

        static final int TEST_SHEET_NUM = 0;

        static final int HEADER_COL_COUNT = 31;

        static final int COL_COUNT = 30;

        static final int ROW_COUNT = 78;

    }
}
