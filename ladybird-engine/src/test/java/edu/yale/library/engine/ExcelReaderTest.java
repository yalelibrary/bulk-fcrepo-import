package edu.yale.library.engine;

import edu.yale.library.engine.excel.ImportEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Exercies Excel reading and underlying abstraction.
 * @see FieldConstants (the underlying abstraction subject to modification)
 */
public class ExcelReaderTest
{
    /**
     *  Contains manually checked fields (e.g. num. of columns). Subject to modification.
     *  <ul>
     *      <li> Should probaby be packaged separately. </li>
     *      <li> Still needs additional fields (some values hardcoded in tests) </li>
     *  </ul>
     */

    static class TestFileConstants
    {
        private final static String TEST_XLS_FILE = "excel/4654-pt1-READY-FOR-INGEST-A.xlsx";

        private final static int TEST_SHEET_NUM = 0;

        private final static int HEADER_COL_COUNT = 31;

        private final static int COL_COUNT = 30;

        private final static int ROW_COUNT = 78;

        private static String getTestFile()
        {
            return TEST_XLS_FILE;
        }
    }

    /**
     * Tests numb of rows and columns
     * @throws Exception
     */
    @Test
    public void testColumnsandRows() throws Exception
    {
        XSSFSheet sheet = getDefaultSheet();
        Iterator<org.apache.poi.ss.usermodel.Row> it = sheet.iterator();
        assertEquals("Expected rows mismatch", sheet.getPhysicalNumberOfRows(), TestFileConstants.ROW_COUNT);

        org.apache.poi.ss.usermodel.Row header = it.next();
        assertEquals("Expected header count mismatch", header.getPhysicalNumberOfCells(),
                TestFileConstants.HEADER_COL_COUNT);

        while (it.hasNext())
        {
            org.apache.poi.ss.usermodel.Row row = it.next();
            assertEquals("Expected columns mismatch in row num. " + row.getRowNum(),
                    row.getPhysicalNumberOfCells(), TestFileConstants.COL_COUNT);
        }
    }

    /**
     * Tests backing representation for a column value
     */
    @Test
    public void testField()
    {
        FieldConstants testField = FieldConstants.FDID90;
        final String testValue = "Architecture -- United States -- 20th century -- (YVRC) {id=61680}";
        ImportEntity.Column column = new ImportEntity().new Column(testField, testValue);
        ImportEntity.Row row = new ImportEntity().new Row();
        row.getColumns().add(column);
        for (final ImportEntity.Column f: row.getColumns())
        {
            assertEquals("Column name mismatch", f.field.getName(), "Subject, topic");
            assertEquals("Column title mismatch", f.field.getTitle(), "Subject, topic{fdid=90}");
        }
    }

    /**
     * Test a particular string value in a column.
     */
    @Test
    public void testContents()
    {
        int cellCount = 0;
        try
        {
            XSSFSheet sheet = getDefaultSheet();
            Iterator<Row> it = sheet.iterator();
            List<ImportEntity.Row> sheetRows = new ArrayList();
            while (it.hasNext())
            {
                ImportEntity.Row sheetRow = new ImportEntity().new Row();
                Row row = it.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    ImportEntity.Column column = new ImportEntity().new Column(getHeader(cellCount),
                            String.valueOf(cellValue(cell)));
                    assertTrue("Incompatible value in column.", valueMatchesFieldFormat(column.field, column.value));

                    if (null == column.value)
                        fail("Column value null in row " + row.getRowNum());

                    sheetRow.getColumns().add(column);
                    cellCount++;
                }
                sheetRows.add(sheetRow);
                cellCount = 0;
            }
            assertEquals("Sheet row count mismatch.", sheetRows.size(), TestFileConstants.ROW_COUNT);
            assertEquals(sheetRows.get(1).getColumns().get(4).value, "Gilchrist, Scott"); //arbitrary check
        }
        catch (IllegalArgumentException e)
        {
            fail(e.getMessage());
        }
    }

    @Deprecated
    private boolean valueMatchesFieldFormat(FieldConstants fieldConstants, String value) //TODO type
    {
         return true; //TODO for now. Will probably call some other class
    }

    //TODO
    private FieldConstants getHeader(int cellCount)
    {
        //something like:
        /*
            if(cellCount == 1)
                return FieldConstants.UNK;

         */
        return FieldConstants.UNK;
    }

    private XSSFSheet getDefaultSheet()
    {
        XSSFSheet sheet = null;
        try
        {
            InputStream TEST_EXCEL_FILE = getClass().getClassLoader()
                    .getResourceAsStream(TestFileConstants.getTestFile());
            XSSFWorkbook workbook = new XSSFWorkbook(TEST_EXCEL_FILE);
            sheet = workbook.getSheetAt(TestFileConstants.TEST_SHEET_NUM);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail("Error reading spreadsheet");
        }
        return sheet;
    }


    //TODO change when type is required
    private Object cellValue(Cell cell)
    {
        switch (cell.getCellType())
        {
            case Cell.CELL_TYPE_BOOLEAN:
                return new Boolean(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_NUMERIC:
                return new Double(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
               return new String(cell.getStringCellValue());
            default:
                throw new IllegalArgumentException("Unknown data type");
        }
    }
}
