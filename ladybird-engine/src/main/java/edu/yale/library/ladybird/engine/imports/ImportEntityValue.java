package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldOccurrence;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * ImportEntityValue represents values and provides helper methods.
 */
public class ImportEntityValue {

    private Logger logger = LoggerFactory.getLogger(ImportEntityValue.class);

    private List<ImportEntity.Row> rowList;
    private static int HEADER_ROW = 0;
    private static int CONTENT_ROW = 1;

    public ImportEntityValue(List<Row> rowList) {
        this.rowList = rowList;
    }

    /**
     * Get all specific column values.
     * @param columnNum column number
     * @return
     */
    public List<Column> getColumnValues(final short columnNum) {
        List<Column> columns = new ArrayList<>();
        for (ImportEntity.Row r: rowList) {
            Column c = r.getColumns().get(columnNum);
            columns.add(c);
        }
        return columns;
    }

    /**
     * Get all column values for a specific column. Assumes only one occurrence.
     * @param fieldConstant
     * @return
     */
    public List<Column> getColumnValues(final FieldConstant fieldConstant) {
        List<Column> columns = new ArrayList<>();
        for (ImportEntity.Row r: getContentRows()) {
            for (Column c: r.getColumns()) {
                if (c.getField().getName().equals(fieldConstant.getName())) {
                    columns.add(c);
                }
            }
        }
        return columns;
    }

    /**
     * Get all indexed column values for a specific FieldConstant. Assumes only one occurrence.
     * @param fieldConstant
     * @return
     */
    public Map<Integer, Column> getColumnValuesWithIds(final FieldConstant fieldConstant) {
        Map<Integer, Column> rowIdMap = new HashMap<>();
        for (int i = 0; i < rowList.size(); i++) {
            for (Column c: rowList.get(i).getColumns()) {
                if (c.getField().getName().equals(fieldConstant.getName())) {
                    rowIdMap.put(i, c);
                }
            }
        }
        return rowIdMap;
    }

    /**
     * Get all indexed by oids column values for a specific FieldConstant. Assumes only one occurrence.
     * @param fieldConstant
     * @return Map<Column c1, Colun c2> where c1 = oid column, c2 = field column
     */
    public Map<Column,  Column> getColumnValuesWithOIds(final FieldConstant fieldConstant) {
        Map<Column, Column> rowIdMap = new HashMap<>();
        int order = getFunctionPosition(FunctionConstants.F1);
        for (int i = 0; i < rowList.size(); i++) {
            Column o = rowList.get(i).getColumns().get(order);
            for (Column c: rowList.get(i).getColumns()) {
                if (c.getField().getName().equals(fieldConstant.getName())) {
                    rowIdMap.put(o, c);
                }
            }
        }
        return rowIdMap;
    }

    /**
     * Get all (Except Exhead) indexed by oids column values for a specific FieldConstant. Assumes only one occurrence.
     * @param fieldConstant
     * @return Map<Column c1, Colun c2> where c1 = oid column, c2 = field column
     */
    public Map<Column, Column> getContentColumnValuesWithOIds(final FieldConstant fieldConstant) {
        Map<Column, Column> rowIdMap = new HashMap<>();
        int order = getFunctionPosition(FunctionConstants.F1);
        for (int i = 1; i < rowList.size(); i++) {
            Column o = rowList.get(i).getColumns().get(order);
            for (Column c: rowList.get(i).getColumns()) {
                if (c.getField().getName().equals(fieldConstant.getName())) {
                    logger.trace("Found match={} with value={}", c.getField().getName(), fieldConstant.getName());
                    logger.trace("Values c1={} c2={}", o, c);
                    rowIdMap.put(o, c);
                }
            }
        }
        return rowIdMap;
    }

    /**
     * Get all columns in a row
     * @param rowNum
     * @return
     */
    public List<Column> getRowColumns(short rowNum) {
        return rowList.get(rowNum).getColumns();
    }

     /**
     * Get all FieldConstants represented. Assumes only one occurrence.
     * @return
     */
    public List<FieldConstant> getAllFieldConstants() {

        if (rowList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        final Row exHeadRow = rowList.get(HEADER_ROW);
        final List<FieldConstant> fieldConstantsList = new ArrayList<>();

        for (final Column c: exHeadRow.getColumns()) {
            fieldConstantsList.add(c.getField());
        }
        return fieldConstantsList;
    }

    /**
     * Determines if a FieldConstant exits in the Exhead row
     * @param f
     * @return
     */
    public boolean fieldConstantsInExhead(final FieldConstant f) {
        final List<Column> columnList = rowList.get(HEADER_ROW).getColumns();
        for (final Column c: columnList) {
            if (c.getField().equals(f)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get FieldConstant count
     * @param f
     * @return
     */
    public FieldOccurrence getFieldConstantsCount(final FieldConstant f) {
        short count = 0;
        final List<Column> columnList = rowList.get(HEADER_ROW).getColumns();
        for (Column c: columnList) {
            if (c.getField().equals(f)) {
                count++;
            }
        }
        if (count == 0) {
            return FieldOccurrence.NEVER;
        } else if (count == 1) {
            return FieldOccurrence.ONCE;
        } else {
            return FieldOccurrence.MULTIPLE;
        }
    }

    /**
     * Get Exhead row
     * @return
     */
    public ImportEntity.Row getHeaderRow() {
        return rowList.get(HEADER_ROW);
    }

    /**
     * Get all rows except the exhead row
     * @return
     */
    public List<ImportEntity.Row> getContentRows() {
        return rowList.subList(CONTENT_ROW, rowList.size());
    }

     /**
     * Get (1st) column number of Function
     * @param f
     * @return
     */
    public int getFunctionPosition(final FieldConstant f) {
        List<Column> columnsList = getHeaderRow().getColumns();
        for (int i = 0; i < columnsList.size(); i++) {
            if (columnsList.get(i).getField().getName().equals(f.getName())) {
                return i;
            }
        }
        throw new NoSuchElementException(f.getName());
    }

    public List<String> getColumnStrings(FunctionConstants functionConstants) {
        final List<ImportEntity.Column> bibIdColumn = getColumnValues(functionConstants);
        final List<String> values = new ArrayList<>();

        for (ImportEntity.Column c : bibIdColumn) {
            values.add(c.getValue().toString());
        }

        return values;
    }

    /**
     * Find value from row
     * @param f FieldConstant
     * @param column row with columns
     * @return first value or empty string
     */
    public static String findColValueFromRow(final FieldConstant f, final List<Column> column) {
        for (final Column<String> col: column) {
            if (col.getField().getName().equals(f.getName())) {
                return col.getValue();
            }
        }
        return "";
    }

    public boolean hasFunction(FunctionConstants... f) {
        BitSet bitSet = new BitSet(f.length);

        for (int i = 0; i < f.length; i++) {
            if (fieldConstantsInExhead(f[i])) {
                bitSet.set(i);
            }
        }
        return bitSet.cardinality() == f.length;
    }


    @Override
    public String toString() {
        return "ImportEntityValue{"
                + "rowList=" + rowList
                + ", HEADER_ROW=" + HEADER_ROW
                + ", CONTENT_ROW=" + CONTENT_ROW
                + '}';
    }
}