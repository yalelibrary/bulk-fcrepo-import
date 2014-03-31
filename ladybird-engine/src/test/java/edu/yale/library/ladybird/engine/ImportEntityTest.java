package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


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


}
