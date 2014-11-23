package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.FieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 */
public class HandleMinter {

    private Logger logger = LoggerFactory.getLogger(HandleMinter.class);

    public ImportEntityValue write(ImportEntityValue importEntityValue) {
        final List<ImportEntity.Column> exheadList = importEntityValue.getHeaderRow().getColumns();
        final FieldConstant fdid111 = new FieldDefinition(111, "");
        final ImportEntity.Column<String> column = new ImportEntity().new Column<>(fdid111, "");
        exheadList.add(column);
        importEntityValue.setHeaderRow(exheadList);
        final List<ImportEntity.Row> rowList = importEntityValue.getContentRows();

        for (ImportEntity.Row row: rowList) {
            try {
                int f104Column = importEntityValue.getFunctionPosition(FunctionConstants.F104);
                String bibId = row.getColumns().get(f104Column).getValue().toString();
                row.getColumns().add(new ImportEntity().new Column<>(fdid111, "http://hdl.handle.net/10079/bibid/" + bibId));
            } catch (Exception e) {
                logger.trace("Error={}", e.getMessage()); //ignore
            }
        }
        importEntityValue.setContentRows(rowList);
        return importEntityValue;
    }
}
