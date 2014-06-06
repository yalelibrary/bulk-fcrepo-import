package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.AbstractDBTest;
import edu.yale.library.ladybird.engine.DefaultFieldDataValidator;
import edu.yale.library.ladybird.engine.FieldDefinitionInitializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;


/**
 *
 */
public class DefaultImportReaderTest extends AbstractDBTest {

    private static class TestFileConstants {
        static final String TEST_XLS_FILE = "excel/4654-pt1-READY-FOR-INGEST-A.xlsx";
        static final int TEST_SHEET_NUM = 0;
        static final int HEADER_COL_COUNT = 31;
        static final int COL_COUNT = 30;
        static final int ROW_COUNT = 78;
    }

    @Test
    public void execute() throws Exception {

        try {
            FieldDefinitionInitializer fieldDefinitionInitializer = new FieldDefinitionInitializer();
            fieldDefinitionInitializer.setInitialFieldDefinitionDb();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed");
        }


        AbstractImportEngine abstractImportProcessor = new DefaultImportEngine();
        List<ImportEntity.Row> rows = abstractImportProcessor.read(getTestSpreadsheeet(), ReadMode.FULL,
                new DefaultFieldDataValidator());
        Assert.assertEquals("Row size mismatch while reading spreadsheet", rows.size(), TestFileConstants.ROW_COUNT);
        assertEquals(rows.get(1).getColumns().get(4).getValue(), "Gilchrist, Scott");
    }

    public SpreadsheetFile getTestSpreadsheeet() throws IOException {
        SpreadsheetFile file = new SpreadsheetFileBuilder()
                .setFileName(TestFileConstants.TEST_XLS_FILE)
                .setAltName("Test spreadsheet")
                .setPath(TestFileConstants.TEST_XLS_FILE)
                .setFileStream(getClass().getClassLoader().getResourceAsStream(TestFileConstants.TEST_XLS_FILE))
                .createSpreadsheetFile();

        return file;
    }

    @Before
    public void init() {
        super.init();
    }

    @After
    public void stopDB() throws SQLException {
        super.stop();
    }

}
