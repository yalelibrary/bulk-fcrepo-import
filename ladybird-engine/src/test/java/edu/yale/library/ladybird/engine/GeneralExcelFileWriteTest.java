package edu.yale.library.ladybird.engine;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Exercises general Excel writing. The test is meant for testing the format of the test file.
 */
public class GeneralExcelFileWriteTest {

    public static final String PATHNAME = asTmp("test_poi_write.xlsx");

    //FIXME create temporary directory, temp file, and delete on exit
    private static String asTmp(final String s) {
        return System.getProperty("user.home") + System.getProperty("file.separator") + s;
    }

    /**
     * Writes and tests number of rows and columns
     *
     * @throws Exception
     */
    @Test
    public void testWrite() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Default Sheet");

        Map<String, Object[]> map = new HashMap<>();
        map.put("1", new Object[]{"No.", "Path", "Oid"});
        map.put("2", new Object[]{1, "/tmp", 58400});
        map.put("3", new Object[]{2, "/tmp2", 5500});
        map.put("4", new Object[]{3, "/tmp3", 7741});

        Set<String> keyset = map.keySet();
        int rowNum = 0;
        for (String key : keyset) {
            Row row = sheet.createRow(rowNum++);
            Object[] objArr = map.get(key);
            int cellNum = 0;
            for (Object o : objArr) {
                Cell cell = row.createCell(cellNum++);
                if (o instanceof Date) {
                    cell.setCellValue((Date) o);
                } else if (o instanceof Boolean) {
                    cell.setCellValue((Boolean) o);
                } else if (o instanceof String) {
                    cell.setCellValue((String) o);
                } else if (o instanceof Integer) {
                    cell.setCellValue((Integer) o);
                }
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(new File(PATHNAME));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // Read back:

        final InputStream EXCEL_FILE = new FileInputStream(new File(PATHNAME));
        final XSSFWorkbook readWorkbook = new XSSFWorkbook(EXCEL_FILE);
        final XSSFSheet readSheet = readWorkbook.getSheetAt(0);

        assertEquals("Expected rows mismatch", readSheet.getPhysicalNumberOfRows(), 4);
    }

}
