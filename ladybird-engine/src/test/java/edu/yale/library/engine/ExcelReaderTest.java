package edu.yale.library.engine;


import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;

public class ExcelReaderTest
{
    private final static int TEST_SHEET_NUM = 0;

    private final static String TEST_XLS_FILE = "excel/4654-pt1-READY-FOR-INGEST-A.xlsx";

    @Test
    public void testContents()
    {
        try
        {
            InputStream TEST_EXCEL_FILE = getClass().getClassLoader()
                    .getResourceAsStream(TEST_XLS_FILE);
            XSSFWorkbook workbook = new XSSFWorkbook(TEST_EXCEL_FILE);
            XSSFSheet sheet = workbook.getSheetAt(TEST_SHEET_NUM);
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    switch (cell.getCellType())
                    {
                    case Cell.CELL_TYPE_BOOLEAN:
                        System.out.print(cell.getBooleanCellValue() + "\t\t");
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        System.out.print(cell.getNumericCellValue() + "\t\t");
                        break;
                    case Cell.CELL_TYPE_STRING:
                        System.out.print(cell.getStringCellValue() + "\t\t");
                        break;
                    }
                }
                System.out.println("");
            }
            TEST_EXCEL_FILE.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testWrite()
    {

    }
}
