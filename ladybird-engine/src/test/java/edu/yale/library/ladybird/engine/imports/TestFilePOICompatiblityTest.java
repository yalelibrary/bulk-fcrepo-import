package edu.yale.library.ladybird.engine.imports;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Meant for testing whether the test file can be read with POI.
 */
public class TestFilePOICompatiblityTest {

    private static class TestFileConstants {
        static final String TEST_XLS_FILE = "excel/4654-pt1-READY-FOR-INGEST-A.xlsx";
        static final int TEST_SHEET_NUM = 0;
        static final int HEADER_COL_COUNT = 31;
        static final int COL_COUNT = 30;
        static final int ROW_COUNT = 78;
    }

    /**
     * Tests number of rows and columns
     *
     * @throws Exception
     */
    @Test
    public void shouldEqualExpectedColumnsandRows() throws IOException {

        InputStream TEST_EXCEL_FILE = getClass().getClassLoader()
                .getResourceAsStream(TestFileConstants.TEST_XLS_FILE);
        XSSFWorkbook workbook = new XSSFWorkbook(TEST_EXCEL_FILE);
        XSSFSheet sheet = workbook.getSheetAt(TestFileConstants.TEST_SHEET_NUM);

        Iterator<org.apache.poi.ss.usermodel.Row> it = sheet.iterator();
        assertEquals("Expected rows mismatch", sheet.getPhysicalNumberOfRows(), TestFileConstants.ROW_COUNT);

        org.apache.poi.ss.usermodel.Row header = it.next();
        assertEquals("Expected header count mismatch", header.getPhysicalNumberOfCells(),
                TestFileConstants.HEADER_COL_COUNT);

        while (it.hasNext()) {
            org.apache.poi.ss.usermodel.Row row = it.next();
            assertEquals("Expected columns mismatch in row num. " + row.getRowNum(),
                    row.getPhysicalNumberOfCells(), TestFileConstants.COL_COUNT);
        }
    }

}
