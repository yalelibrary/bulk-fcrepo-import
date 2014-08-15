package edu.yale.library.ladybird.engine.imports;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpreadsheetUtil {
    /**
     * Helper returns cell value as an object
     *
     * @param cell preadsheet cell
     * @return Object wrapping primitive or string
     */
    static Object getCellValue(final Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return (long) cell.getNumericCellValue(); //TODO note int. FIXME
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_BLANK:
                return "";
            default:
                throw new IllegalArgumentException("Unknown data type:" + cell.getCellType());
        }
    }

    public static List<String> getColumnValues(InputStream TEST_EXCEL_FILE, String column, int sheetNum) throws Exception {
        List<String> list = new ArrayList<>();

        XSSFWorkbook workbook = new XSSFWorkbook(TEST_EXCEL_FILE);
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        Iterator<Row> it = sheet.iterator();
        org.apache.poi.ss.usermodel.Row header = it.next();
        Iterator<Cell> headerItr = header.cellIterator();
        int pos = -1;

        while (headerItr.hasNext()) {
            Cell cell = headerItr.next();
            String s = (String) getCellValue(cell);
            if (s.equals(column)) {
                if (pos == -1) {
                    pos = cell.getColumnIndex();
                } else { //multiple
                    throw new Exception("Wrong column header");
                }
                headerItr.next();
            }
        }

        while (it.hasNext()) {
            Row row = it.next();
            Cell col = row.getCell(pos);
            String t = (String) getCellValue(col);
            if (!t.isEmpty()) {
                list.add(t);
            }
        }

        return list;
    }
}
