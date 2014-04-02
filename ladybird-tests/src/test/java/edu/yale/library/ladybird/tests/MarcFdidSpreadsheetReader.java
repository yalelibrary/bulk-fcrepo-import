package edu.yale.library.ladybird.tests;

import edu.yale.library.ladybird.kernel.beans.FieldMarcMapping;
import edu.yale.library.ladybird.kernel.beans.FieldMarcMappingBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used for reading test fdid-marc mappings
 */
public class MarcFdidSpreadsheetReader {

    public List<FieldMarcMapping> readMarcMapping(String fileName, int sheetNum) throws IOException {

        final InputStream spreadsheetFile = getClass().getClassLoader()
                .getResourceAsStream(fileName);
        final XSSFWorkbook workbook = new XSSFWorkbook(spreadsheetFile);
        final XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        final Iterator<Row> it = sheet.iterator();

        it.next(); //ignore header column for reading

        final List<FieldMarcMapping> mappingList = new ArrayList<>();

        while (it.hasNext()) {
            final Row row = it.next();
            final Iterator<Cell> cellIterator = row.cellIterator();

            final FieldMarcMappingBuilder fieldMarcMappingBuilder = new FieldMarcMappingBuilder();
            //TODO k880, concatOn
            final String k1 = String.valueOf((int) cellIterator.next().getNumericCellValue());
            final String k2 = cellIterator.next().getStringCellValue();
            final String k880 = cellIterator.next().getStringCellValue(); //TODO
            final int fdid = (int) cellIterator.next().getNumericCellValue();
            final String delim = cellIterator.next().getStringCellValue();
            final String concat = cellIterator.next().getStringCellValue();
            final String concatOn = ""; //TODO

            final FieldMarcMapping fieldMarcMapping = fieldMarcMappingBuilder
                    .setK1(k1)
                    .setK2(k2)
                    .setFdid(fdid)
                    .setDelim(delim)
                    .setConcat(concat)
                    .createFieldMarcMapping();
            mappingList.add(fieldMarcMapping);
        }
        return mappingList;
    }

}
