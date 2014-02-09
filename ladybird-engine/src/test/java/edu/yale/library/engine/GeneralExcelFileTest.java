package edu.yale.library.engine;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * Exercises general Excel reading. The test is meant for testing the format of the test file.
 *
 */
public class GeneralExcelFileTest
{

    /**
     * Tests number of rows and columns
     * @throws Exception
     */
    @Test
    public void testColumnsandRows() throws IOException
    {

        InputStream TEST_EXCEL_FILE = getClass().getClassLoader()
                .getResourceAsStream(FileConstants.TEST_XLS_FILE);
        XSSFWorkbook workbook = new XSSFWorkbook(TEST_EXCEL_FILE);
        XSSFSheet sheet = workbook.getSheetAt(FileConstants.TEST_SHEET_NUM);

        Iterator<org.apache.poi.ss.usermodel.Row> it = sheet.iterator();
        assertEquals("Expected rows mismatch", sheet.getPhysicalNumberOfRows(), FileConstants.ROW_COUNT);

        org.apache.poi.ss.usermodel.Row header = it.next();
        assertEquals("Expected header count mismatch", header.getPhysicalNumberOfCells(),
                FileConstants.HEADER_COL_COUNT);

        while (it.hasNext())
        {
            org.apache.poi.ss.usermodel.Row row = it.next();
            assertEquals("Expected columns mismatch in row num. " + row.getRowNum(),
                    row.getPhysicalNumberOfCells(), FileConstants.COL_COUNT);
        }
    }

}
