package edu.yale.library.engine;

import edu.yale.library.engine.model.DefaultFieldDataValidator;
import edu.yale.library.engine.model.ReadMode;
import edu.yale.library.engine.imports.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 *
 */
public class DefaultImportReaderTest
{

    @Test
    public void execute() throws Exception
    {
        AbstractImportEngine abstractImportProcessor = new DefaultImportEngine();
        List<ImportEntity.Row> rows = abstractImportProcessor.read(getTestSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        assertEquals("Row size mismatch while reading spreadsheet", rows.size(), FileConstants.ROW_COUNT);
        assertEquals(rows.get(1).getColumns().get(4).getValue(), "Gilchrist, Scott");
    }

    public SpreadsheetFile getTestSpreadsheeet() throws IOException
    {
        SpreadsheetFile file = new SpreadsheetFile(FileConstants.TEST_XLS_FILE, "Test spreadsheet",
                FileConstants.TEST_XLS_FILE, getClass().getClassLoader().getResourceAsStream(FileConstants.TEST_XLS_FILE));

        return file;
    }


}
