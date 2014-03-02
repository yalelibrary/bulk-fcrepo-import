package edu.yale.library.engine.imports;

import edu.yale.library.engine.model.ReadMode;
import edu.yale.library.engine.model.ImportReaderValidationException;
import edu.yale.library.engine.model.FieldConstant;
import edu.yale.library.engine.model.FieldDefinitionValue;
import edu.yale.library.engine.model.UnknownFunctionException;
import edu.yale.library.engine.model.FunctionConstants;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Read Processor. Subject to modification.
 */
public final class ImportReader {

    private final Logger logger = getLogger(this.getClass());
    private final SpreadsheetFile file;
    private final int sheetNumber; //assumes one sheet
    private ReadMode readMode;

    public ImportReader(SpreadsheetFile file, int sheetNumber, ReadMode readMode) {
        this.file = file;
        this.sheetNumber = sheetNumber;
        this.readMode = readMode;
    }

    public List<ImportEntity.Row> processSheet() throws ImportReaderValidationException, IOException {
        List<ImportEntity.Row> sheetRows = new ArrayList<>();

        // value list hods column function values. Could be replaced with a map.
        List<FieldConstant> valueMap = new ArrayList<>();

        try {
            logger.debug("Processing a sheet of={}", file);
            XSSFSheet sheet = getDefaultSheet();
            final Iterator<Row> it = sheet.iterator();

            //read first row
            Row firstRow = it.next();
            final Iterator<Cell> firstRowCellIterator = firstRow.cellIterator();
            final ImportEntity.Row headerSheetRow = new ImportEntity().new Row();

            logger.debug(FieldDefinitionValue.getFieldDefMap().toString());

            while (firstRowCellIterator.hasNext()) {
                final Cell cell = firstRowCellIterator.next();

                // Reader Header value.
                try {
                    FieldConstant f = getFieldConstant(String.valueOf(cellValue(cell)));
                    valueMap.add(f);
                    ImportEntity.Column<String> column = new ImportEntity().new Column<>(f,
                            String.valueOf(cellValue(cell)));
                    headerSheetRow.getColumns().add(column);
                } catch (UnknownFunctionException unknownFunction) {
                    if (this.readMode == ReadMode.HALT) {
                        logger.error("Unknown field column in header." + unknownFunction.getMessage());
                        ImportReaderValidationException importReaderValidationException = new ImportReaderValidationException(unknownFunction);
                        importReaderValidationException.initCause(unknownFunction);
                        throw importReaderValidationException;
                    }
                    logger.debug("Unknown header value. " + unknownFunction.getMessage());
                    valueMap.add(FunctionConstants.UNK);
                }
            }
            //add header row:
            sheetRows.add(headerSheetRow);

            //iterate body //FIXME poential bug(s). check empty columnns, and use map etc instead of list
            int cellCount = 0;
            while (it.hasNext()) {
                ImportEntity.Row contentsSheetRow = new ImportEntity().new Row();
                Row row = it.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    ImportEntity.Column<String> column = new ImportEntity().new Column<>(valueMap.get(cellCount),
                            String.valueOf(cellValue(cell)));
                    contentsSheetRow.getColumns().add(column);
                    cellCount++;
                }
                sheetRows.add(contentsSheetRow);
                cellCount = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return sheetRows;
    }

    /**
     * @param cellValue
     * @return a FieldConstant
     * @throws UnknownFunctionException
     * @see FunctionConstants
     */
    private FieldConstant getFieldConstant(String cellValue) throws UnknownFunctionException {
        if (FieldDefinitionValue.getFieldDefMap().containsKey(cellValue.trim())) //e.g. Note{fdid=80}
        {
            return FieldDefinitionValue.getFieldDefMap().get(cellValue.trim()); //?
        }

        try {
            String normCellString = normalizeFunctionConst(cellValue);
            FieldConstant fieldConst = FunctionConstants.valueOf(normCellString);
            return fieldConst;
        } catch (IllegalArgumentException e) {
            throw new UnknownFunctionException("Specified cell not a recognized function or fdid= " + cellValue);
        }
    }

    /**
     * Removes brackets etc for now
     *
     * @param cellValue
     * @return normalized string
     */
    private String normalizeFunctionConst(String cellValue) {
        return cellValue.replace("{", "").replace("}", "");
    }

    /**
     * TODO change return type
     * Returns cell value as an object
     *
     * @param cell
     * @return Object wrapping primitive or string
     */
    private Object cellValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                throw new IllegalArgumentException("Unknown data type");
        }
    }

    /**
     * Read default sheet (0 for now)
     *
     * @return
     * @throws IOException
     */
    private XSSFSheet getDefaultSheet() throws IOException {
        logger.debug("Reading sheet={}", file.getFileName());
        XSSFWorkbook workbook = new XSSFWorkbook(file.getFileStream());
        XSSFSheet sheet = workbook.getSheetAt(sheetNumber);
        return sheet;
    }
}
