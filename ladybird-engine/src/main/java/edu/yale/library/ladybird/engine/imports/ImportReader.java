package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.UnknownFieldConstantException;
import edu.yale.library.ladybird.entity.FieldConstant;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Spreadsheet reader.
 */
public final class ImportReader {

    private static final Logger logger = getLogger(ImportReader.class);

    private final Spreadsheet file;
    private final int sheetNumber;
    private ReadMode readMode;

    public ImportReader(Spreadsheet file, int sheetNumber, ReadMode readMode) {
        this.file = file;
        this.sheetNumber = sheetNumber;
        this.readMode = readMode;
    }

    /**
     * Process a single sheet SpreadsheetFile.sheetNumber of SpreadsheetFile file
     *
     * @return data structure containing all the data from the spreadsheet
     * @throws ImportReaderValidationException
     * @throws IOException
     */
    public List<Import.Row> read() throws ImportReaderValidationException, IOException {
        logger.debug("Processing sheetNumber={} of={}", sheetNumber, file);

        try {
            final XSSFSheet sheet = file.getDefaultSheet(sheetNumber);
            final int physicalRows = sheet.getPhysicalNumberOfRows();
            final List<Import.Row> sheetRows = new ArrayList<>(physicalRows);
            final Iterator<Row> it = sheet.iterator();
            final Row firstRow = it.next();
            final Iterator<Cell> firstRowCellItr = firstRow.cellIterator();
            final Import.Row headerRow = new Import().new Row();

            int exHeadRowCellCount = 0;

            final List<FieldConstant> valueMap = new ArrayList<>(50);

            while (firstRowCellItr.hasNext()) {
                final Cell cell = firstRowCellItr.next();

                // read header:
                try {
                    FieldConstant f = FieldConstantUtil.getFieldConstant(String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                    valueMap.add(f);

                    final Import.Column<String> column
                            = new Import().new Column<>(f, String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                    headerRow.getColumns().add(column);
                } catch (UnknownFieldConstantException unknownFunction) {
                    if (this.readMode == ReadMode.HALT) {
                        logger.error("Unknown field column in header={}", unknownFunction.getMessage());
                        final ImportReaderValidationException importReaderValidationException =
                                new ImportReaderValidationException(unknownFunction);
                        importReaderValidationException.initCause(unknownFunction);
                        throw importReaderValidationException;
                    }
                    logger.debug("Warning: ({}, {})={}", file, sheetNumber, unknownFunction.getMessage());
                    //means probably fdid has not been added in the properties

                    valueMap.add(FunctionConstants.UNK); //TODO shouldn't be used to represent both unknown func and fdid

                    //logger.trace("Adding UNK in 1st row for this unrecognized FieldConstant");
                    // added to keep the exhead and contents col. the same.

                    final Import.Column<String> column = new Import()
                            .new Column<>(FunctionConstants.UNK, String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                    headerRow.getColumns().add(column);
                } catch (Exception e) {
                    logger.error("Unknown error iterating header row", e);
                }

                exHeadRowCellCount++;
            }
            logger.trace("Exhead cell count={}", exHeadRowCellCount);
            // add header row:
            sheetRows.add(headerRow);

            // iterate body:
            int cellCount = 0;
            final Import importEntity = new Import();

            try {
                while (it.hasNext()) {
                    final Import.Row contentsSheetRow = importEntity.new Row();
                    final Row row = it.next();
                    int lastColumn = Math.max(row.getLastCellNum(), exHeadRowCellCount);

                    for (int cn = 0; cn < lastColumn && cn < exHeadRowCellCount; cn++) {
                        Cell cell = row.getCell(cn, Row.RETURN_BLANK_AS_NULL);

                        // handle null fields, otherwise values will get mushed
                        if (cell == null) {
                            contentsSheetRow.getColumns().add(ImportValue.getBlankColumn(valueMap.get(cn)));
                            cellCount++;
                        } else {
                            final Import.Column<String> column = importEntity.new Column<>(valueMap.get(cellCount),
                                    String.valueOf(SpreadsheetUtil.getCellValue(cell)));
                            contentsSheetRow.getColumns().add(column);
                            cellCount++;
                        }
                    }
                    Import.Row evalRow = new Import()
                            .new Row(Collections.unmodifiableList(contentsSheetRow.getColumns()));

                    if (!allFieldsNull(evalRow)) {
                        sheetRows.add(contentsSheetRow);
                    }

                    if (sheetRows.size() % 10000 == 0) {
                        logger.debug("Read so far rows={} for= {}", sheetRows.size(), file.getFileName());
                    }

                    cellCount = 0;
                }
                logger.debug("Done reading sheetNumber={} of={} size={}", sheetNumber, file, sheetRows.size());
                return sheetRows;
            } catch (IllegalArgumentException e) {
                logger.error("Error reading cell column={}, error={}", cellCount + 1, e.getMessage());
                throw new IllegalArgumentException(e);
            }
        }  catch (IOException | IllegalArgumentException e) {
            logger.error("Error reading", e);
            throw e;
        }
    }

    private boolean allFieldsNull(final Import.Row row) {
        List<Import.Column> cols = row.getColumns();
        boolean blank = true;

        for (final Import.Column<String> c : cols) {
            if (c.getValue() == null) {
                logger.error("Null column value found");
                throw new IllegalArgumentException("Null col value");
            }

            if (!c.getValue().isEmpty()) {
                blank = false;
                return blank;
            }
        }
        return blank;
    }

}
