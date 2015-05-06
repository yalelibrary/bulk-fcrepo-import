package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.entity.FieldConstant;
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
        Import.Column<Integer> column = new Import().new Column<>(testField, 2555445);
        Import.Row row = new Import().new Row();
        row.getColumns().add(column);
        for (final Import.Column<Integer> f : row.getColumns()) {
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
        Import.Column<String> c1 = getTestColumn(FunctionConstants.F1, "333993");
        Import.Column<String> c2 = getTestColumn(FunctionConstants.F1, "333993");
        assertTrue(c1.equals(c2));
    }

    public Import.Column getTestColumn(final FieldConstant f, final String value) {
        return new Import().new Column<>(f, value);
    }


}
