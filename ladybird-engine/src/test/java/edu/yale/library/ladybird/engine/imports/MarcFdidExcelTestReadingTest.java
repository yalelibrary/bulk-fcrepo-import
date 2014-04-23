package edu.yale.library.ladybird.engine.imports;


import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;


public class MarcFdidExcelTestReadingTest {

    @Test
    public void shouldContainSpecificRowsColumns() throws IOException {

        InputStream TEST_EXCEL_FILE = getClass().getClassLoader()
                .getResourceAsStream(FileConstants.TEST_XLS_FILE);
        XSSFWorkbook workbook = new XSSFWorkbook(TEST_EXCEL_FILE);
        XSSFSheet sheet = workbook.getSheetAt(FileConstants.TEST_SHEET_NUM);

        Iterator<org.apache.poi.ss.usermodel.Row> it = sheet.iterator();
        assertEquals("Expected rows mismatch", sheet.getPhysicalNumberOfRows(), FileConstants.ROW_COUNT);

        org.apache.poi.ss.usermodel.Row header = it.next();
        assertEquals("Expected header count mismatch", header.getPhysicalNumberOfCells(),
                FileConstants.HEADER_COL_COUNT);

        while (it.hasNext()) {
            org.apache.poi.ss.usermodel.Row row = it.next();
            assertEquals("Expected columns mismatch in row num. " + row.getRowNum(),
                    row.getPhysicalNumberOfCells(), FileConstants.COL_COUNT);
        }
    }

    private static class FileConstants {
        static final String TEST_XLS_FILE = "excel/field-marc-mappings.xlsx";
        static final int TEST_SHEET_NUM = 0;
        static final int HEADER_COL_COUNT = 7;
        static final int COL_COUNT = 7;
        static final int ROW_COUNT = 87;
    }

}
