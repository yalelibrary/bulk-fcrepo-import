package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DefaultImportReaderTest extends AbstractDBTest {

    private static class TestFileConstants {
        static final String TEST_XLS_FILE = "excel/4654-pt1-READY-FOR-INGEST-A.xlsx";
        static final int ROW_COUNT = 78;
    }

    @Test
    public void execute() throws Exception {
        AbstractImportEngine abstractImportProcessor = new DefaultImportEngine(0, 1); //TODO chk params logic
        List<Import.Row> rows = abstractImportProcessor.read(getTestSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        Assert.assertEquals("Row size mismatch while reading spreadsheet", rows.size(), TestFileConstants.ROW_COUNT);
        assertEquals(rows.get(1).getColumns().get(4).getValue(), "Gilchrist, Scott");
    }

    public SpreadsheetFile getTestSpreadsheeet() throws IOException {
        return new SpreadsheetFileBuilder()
                .filename(TestFileConstants.TEST_XLS_FILE)
                .altname("Test spreadsheet")
                .filepath(TestFileConstants.TEST_XLS_FILE)
                .stream(getClass().getClassLoader().getResourceAsStream(TestFileConstants.TEST_XLS_FILE))
                .create();
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stop() throws SQLException {
       // super.stop();
    }

}
