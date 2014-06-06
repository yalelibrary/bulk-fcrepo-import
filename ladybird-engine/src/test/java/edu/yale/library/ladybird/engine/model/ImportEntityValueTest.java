package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.entity.FieldDefinition;
import org.junit.Test;

import java.util.*;


import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImportEntityValueTest {

    Logger logger = LoggerFactory.getLogger(ImportEntityValueTest.class);

    @Test
    public void getColumnValues() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        final List<Column> columnsList = importEntityValue.getColumnValues((short) 0);
        assertTrue(columnsList.get(0).getField().equals(FunctionConstants.F1));
    }

    @Test
    public void getColumnValuesWithIds() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        final Map<Integer, Column> importColumn = importEntityValue.getColumnValuesWithIds(FunctionConstants.F1);
        assertTrue(importColumn.containsKey(new Integer(0)));
        final Column column = importColumn.get(new Integer(0));
        assertTrue(column.getField().equals(FunctionConstants.F1));
    }

    @Test
    public void getColumnValuesWithOIds() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        final Map<Column, Column> map = importEntityValue.getColumnValuesWithOIds(FunctionConstants.F1);
        assertTrue(map.containsKey(getTestColumn(FunctionConstants.F1, "333993")));
        final Column column = map.get(getTestColumn(FunctionConstants.F1, "333993"));
        assertTrue(column.getField().equals(FunctionConstants.F1));
    }

    @Test
    public void shouldFindExHead() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        assertTrue(importEntityValue.fieldConstantsInExhead(FunctionConstants.F1));
        assertFalse(importEntityValue.fieldConstantsInExhead(FunctionConstants.F3));
    }

    @Test
    public void shouldGetColumnsFromRow() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        final List<Column> columnList = importEntityValue.getRowColumns((short) 0);
        assertTrue(columnList.size() == 1);
    }

    @Test
    public void shouldFetchAllFieldConstants() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        final List<FieldConstant> fieldConstants = importEntityValue.getAllFieldConstants();
        assertTrue(fieldConstants.size() == 1);
        assertTrue(fieldConstants.get(0).equals(FunctionConstants.F1));
    }

    @Test
    public void shouldFetchFunctionConstantAndFdid() { //i.e. all FieldConstants
        final ImportEntityValue importEntityValue = getTestMultipleRowImportEntityValue();
        final List<FieldConstant> fieldConstants = importEntityValue.getAllFieldConstants();
        assertTrue(fieldConstants.size() == 2);
        assertTrue(fieldConstants.get(0).equals(FunctionConstants.F1));
        FieldDefinition fieldDefinition = (FieldDefinition) fieldConstants.get(1);
        assertEquals("Value mismatch", fieldDefinition.getFdid(), 69);
    }

    @Test
    public void shouldFetchContentRowsFieldConstants() {
        final ImportEntityValue importEntityValue = getTestMultipleRowImportEntityValue();
        FieldDefinitionValue fdValue = new FieldDefinitionValue(69, "tt");

        final Map<Column,Column> map = importEntityValue.getContentColumnValuesWithOIds(fdValue);
        //logger.debug(map.toString());

        Set<Column> keySet = map.keySet();

        // Column{field=F1, value=333993}=Column{field=FieldDefinition{fdid=69, acid=0, handle=''}, value=name}}

        for (Column c: keySet) {
            assertTrue(c.getField().getName() == "F1");
            assertEquals(c.getValue(), "333993");

            Column v = map.get(c);

            assertEquals(v.getField().getName(),"");
            assertEquals(v.getValue(),"name");
        }
    }

    @Test
    public void shouldEqualFieldConstantsCount() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        final FieldOccurrence fieldOccurrence = importEntityValue.getFieldConstantsCount(FunctionConstants.F1);
        assertTrue(fieldOccurrence.equals(FieldOccurrence.ONCE));
    }

    @Test
    public void getFunctionPosition() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        final int position = importEntityValue.getFunctionPosition(FunctionConstants.F1);
        assertTrue(position == 0);
    }

    @Test
    public void showThrowExceptionWithUnknownFunction() {
        final ImportEntityValue importEntityValue = getTestSingleColumnRowImportEntityValue();
        try {
            final int position = importEntityValue.getFunctionPosition(FunctionConstants.F1);
            assertTrue(position == 0);
        } catch (NoSuchElementException e) {
            fail("Mishandled function location");
        }

        try {
            importEntityValue.getFunctionPosition(FunctionConstants.F3);
            fail("Mishandled function location");
        } catch (NoSuchElementException e) {
            assert (e.getMessage().contains(FunctionConstants.F3.getName()));
        }
    }

    @Test
    public void shouldGetContentRows() throws Exception {
        final ImportEntityValue importEntityvalue = getTestExheadAndContentRowsImportEntityValue();
        final List<Row> rowsList = importEntityvalue.getContentRows();
        assertTrue(rowsList.size() == 1);
    }

    @Test
    public void shouldGetExHeadOnly() {
        ImportEntityValue importEntityValue = getTestExheadAndContentRowsImportEntityValue();
        final Row headerRow = importEntityValue.getHeaderRow();
        for (final Column c: headerRow.getColumns()) {
            if (c.getValue().toString().length() != 0) {
                fail("Wrong value found");
            }
        }
    }

    private ImportEntityValue getTestSingleColumnRowImportEntityValue() {
        final List<ImportEntity.Column> columns = new ArrayList<>();
        columns.add(getTestColumn(FunctionConstants.F1, "333993"));
        final Row row = getTestRow(columns);
        final ImportEntityValue importEntityValue = new ImportEntityValue(Collections.singletonList(row));
        return importEntityValue;
    }

    private ImportEntityValue getTestExheadAndContentRowsImportEntityValue() {
        final List<ImportEntity.Column> columnList1 = new ArrayList<>();
        columnList1.add(getTestColumn(FunctionConstants.F1, ""));
        final Row row = getTestRow(columnList1);

        final List<ImportEntity.Column> columnList2 = new ArrayList<>();
        columnList2.add(getTestColumn(FunctionConstants.F1, "333993"));
        final Row row2 = getTestRow(columnList2);

        final List<Row> importRows = new ArrayList<>();
        importRows.add(row);
        importRows.add(row2);

        final ImportEntityValue importEntityValue = new ImportEntityValue(importRows);
        return importEntityValue;
    }

    private ImportEntityValue getTestMultipleRowImportEntityValue() {
        final List<ImportEntity.Column> columnList1 = new ArrayList<>();

        columnList1.add(getTestColumn(FunctionConstants.F1, ""));
        columnList1.add(new ImportEntity().new Column<>(new FieldDefinitionValue(69, ""), ""));

        final Row row = getTestRow(columnList1);

        final List<ImportEntity.Column> columnList2 = new ArrayList<>();
        columnList2.add(getTestColumn(FunctionConstants.F1, "333993"));
        columnList2.add(new ImportEntity().new Column<>(new FieldDefinitionValue(69, ""), "name"));

        final Row row2 = getTestRow(columnList2);

        final List<Row> importRows = new ArrayList<>();
        importRows.add(row);
        importRows.add(row2);

        final ImportEntityValue importEntityValue = new ImportEntityValue(importRows);
        return importEntityValue;
    }

    private Column getTestColumn(final FieldConstant f, final String value) {
        return new ImportEntity().new Column<>(f, value);
    }

    private Row getTestRow(final List<Column> columns) {
        Row row = new ImportEntity().new Row();
        row.setColumns(columns);
        return row;
    }
}