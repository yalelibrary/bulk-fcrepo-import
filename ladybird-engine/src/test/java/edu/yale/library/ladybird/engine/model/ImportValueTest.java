package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.Import.Column;
import edu.yale.library.ladybird.engine.imports.Import.Row;
import edu.yale.library.ladybird.engine.imports.ImportValue;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImportValueTest {

    @Test
    public void getColumnValues() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        final List<Column> columnsList = importValue.getColumnValues((short) 0);
        assertTrue(columnsList.get(0).getField().equals(FunctionConstants.F1));
    }

    @Test
    public void getColumnValuesWithIds() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        final Map<Integer, Column> importColumn = importValue.getColumnValuesWithIds(FunctionConstants.F1);
        assertTrue(importColumn.containsKey(new Integer(0)));
        final Column column = importColumn.get(new Integer(0));
        assertTrue(column.getField().equals(FunctionConstants.F1));
    }

    @Test
    public void getColumnValuesWithOIds() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        final Map<Column, Column> map = importValue.getColumnValuesWithOIds(FunctionConstants.F1);
        assertTrue(map.containsKey(getTestColumn(FunctionConstants.F1, "333993")));
        final Column column = map.get(getTestColumn(FunctionConstants.F1, "333993"));
        assertTrue(column.getField().equals(FunctionConstants.F1));
    }

    @Test
    public void shouldFindExHead() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        assertTrue(importValue.fieldConstantsInExhead(FunctionConstants.F1));
        assertFalse(importValue.fieldConstantsInExhead(FunctionConstants.F3));
    }

    @Test
    public void shouldGetColumnsFromRow() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        final List<Column> columnList = importValue.getRowColumns((short) 0);
        assertTrue(columnList.size() == 1);
    }

    @Test
    public void shouldFetchAllFieldConstants() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        final List<FieldConstant> fieldConstants = importValue.getAllFieldConstants();
        assertTrue(fieldConstants.size() == 1);
        assertTrue(fieldConstants.get(0).equals(FunctionConstants.F1));
    }

    @Test
    public void shouldFetchFunctionConstantAndFdid() { //i.e. all FieldConstants
        final ImportValue importValue = getTestMultipleRowImportEntityValue();
        final List<FieldConstant> fieldConstants = importValue.getAllFieldConstants();
        assertTrue(fieldConstants.size() == 2);
        assertTrue(fieldConstants.get(0).equals(FunctionConstants.F1));
        FieldDefinition fieldDefinition = (FieldDefinition) fieldConstants.get(1);
        assertEquals("Value mismatch", fieldDefinition.getFdid(), 69);
    }

    @Test
    public void shouldFetchContentRowsFieldConstants() {
        final ImportValue importValue = getTestMultipleRowImportEntityValue();
        FieldDefinition fdValue = new FieldDefinition(69, "tt");

        final Map<Column, Column> map = importValue.getContentColumnValuesWithOIds(fdValue);
        //logger.debug(map.toString());

        Set<Column> keySet = map.keySet();

        // Column{field=F1, value=333993}=Column{field=FieldDefinition{fdid=69, acid=0, handle=''}, value=name}}

        for (Column c: keySet) {
            assertTrue(c.getField().getName() == "F1");
            assertEquals(c.getValue(), "333993");

            Column v = map.get(c);

            assertEquals(v.getField().getName(), "fdid=69");
            assertEquals(v.getValue(), "name");
        }
    }

    @Test
    public void shouldEqualFieldConstantsCount() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        final FieldOccurrence fieldOccurrence = importValue.getFieldConstantsCount(FunctionConstants.F1);
        assertTrue(fieldOccurrence.equals(FieldOccurrence.ONCE));
    }

    @Test
    public void getFunctionPosition() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        final int position = importValue.getFunctionPosition(FunctionConstants.F1);
        assertTrue(position == 0);
    }

    @Test
    public void showThrowExceptionWithUnknownFunction() {
        final ImportValue importValue = getTestSingleColumnRowImportEntityValue();
        try {
            final int position = importValue.getFunctionPosition(FunctionConstants.F1);
            assertTrue(position == 0);
        } catch (NoSuchElementException e) {
            fail("Mishandled function location");
        }

        try {
            importValue.getFunctionPosition(FunctionConstants.F3);
            fail("Mishandled function location");
        } catch (NoSuchElementException e) {
            assert (e.getMessage().contains(FunctionConstants.F3.getName()));
        }
    }

    @Test
    public void shouldGetContentRows() throws Exception {
        final ImportValue importEntityvalue = getTestExheadAndContentRowsImportEntityValue();
        final List<Row> rowsList = importEntityvalue.getContentRows();
        assertTrue(rowsList.size() == 1);
    }

    @Test
    public void shouldGetExHeadOnly() {
        ImportValue importValue = getTestExheadAndContentRowsImportEntityValue();
        final Row headerRow = importValue.getHeaderRow();
        for (final Column c: headerRow.getColumns()) {
            if (c.getValue().toString().length() != 0) {
                fail("Wrong value found");
            }
        }
    }

    @Test
    public void shouldGetMultipleFunctions() {
        ImportValue importValue = getTestTripleEntityValue();
        assert (importValue.hasFunction(FunctionConstants.F1, FunctionConstants.F40, FunctionConstants.F104) == true);
    }

    private ImportValue getTestSingleColumnRowImportEntityValue() {
        final List<Import.Column> columns = new ArrayList<>();
        columns.add(getTestColumn(FunctionConstants.F1, "333993"));
        final Row row = getTestRow(columns);
        final ImportValue importValue = new ImportValue(Collections.singletonList(row));
        return importValue;
    }

    private ImportValue getTestTripleEntityValue() {
        final List<Import.Column> columns = new ArrayList<>();
        columns.add(getTestColumn(FunctionConstants.F1, "333993"));
        columns.add(getTestColumn(FunctionConstants.F40, "DELETE"));
        columns.add(getTestColumn(FunctionConstants.F104, "11"));

        final Row row = getTestRow(columns);
        final ImportValue importValue = new ImportValue(Collections.singletonList(row));
        return importValue;
    }

    private ImportValue getTestExheadAndContentRowsImportEntityValue() {
        final List<Import.Column> columnList1 = new ArrayList<>();
        columnList1.add(getTestColumn(FunctionConstants.F1, ""));
        final Row row = getTestRow(columnList1);

        final List<Import.Column> columnList2 = new ArrayList<>();
        columnList2.add(getTestColumn(FunctionConstants.F1, "333993"));
        final Row row2 = getTestRow(columnList2);

        final List<Row> importRows = new ArrayList<>();
        importRows.add(row);
        importRows.add(row2);

        final ImportValue importValue = new ImportValue(importRows);
        return importValue;
    }

    private ImportValue getTestMultipleRowImportEntityValue() {
        final List<Import.Column> columnList1 = new ArrayList<>();

        columnList1.add(getTestColumn(FunctionConstants.F1, ""));
        columnList1.add(new Import().new Column<>(new FieldDefinition(69, ""), ""));

        final Row row = getTestRow(columnList1);

        final List<Import.Column> columnList2 = new ArrayList<>();
        columnList2.add(getTestColumn(FunctionConstants.F1, "333993"));
        columnList2.add(new Import().new Column<>(new FieldDefinition(69, ""), "name"));

        final Row row2 = getTestRow(columnList2);

        final List<Row> importRows = new ArrayList<>();
        importRows.add(row);
        importRows.add(row2);

        final ImportValue importValue = new ImportValue(importRows);
        return importValue;
    }

    private Column getTestColumn(final FieldConstant f, final String value) {
        return new Import().new Column<>(f, value);
    }

    private Row getTestRow(final List<Column> columns) {
        Row row = new Import().new Row();
        row.setColumns(columns);
        return row;
    }
}