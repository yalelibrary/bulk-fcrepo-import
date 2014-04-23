package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ImportEntityTest {
    /**
     * Tests backing representation for a column value
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testField() {
        FunctionConstants testField = FunctionConstants.F1;
        ImportEntity.Column<Integer> column = new ImportEntity().new Column<>(testField, 2555445);
        ImportEntity.Row row = new ImportEntity().new Row();
        row.getColumns().add(column);
        for (final ImportEntity.Column<Integer> f : row.getColumns()) {
            assertEquals("Column name mismatch", f.getField().getName(), "F1");
            assertEquals("Column name mismatch", f.getField().getTitle(), "Oid");
            assertEquals("Column value mismatch", f.getValue(), (Integer) 2555445);
        }
    }

    @Test
    public void testEquals() {
        FieldConstant f1 = FunctionConstants.F1;
        FieldConstant f2 = FunctionConstants.F1;
        assertTrue(f1 == f2);
        ImportEntity.Column<String> c1 = getTestColumn(FunctionConstants.F1, "333993");
        ImportEntity.Column<String> c2 = getTestColumn(FunctionConstants.F1, "333993");
        assertTrue(c1.equals(c2));
    }

    public ImportEntity.Column getTestColumn(final FieldConstant f, final String value) {
        return new ImportEntity().new Column<>(f, value);
    }


}
