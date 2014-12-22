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

    public ImportValue write(ImportValue importValue) {
        final List<Import.Column> exheadList = importValue.getHeaderRow().getColumns();
        final FieldConstant fdid111 = new FieldDefinition(111, "");
        final Import.Column<String> column = new Import().new Column<>(fdid111, "");
        exheadList.add(column);
        importValue.setHeaderRow(exheadList);
        final List<Import.Row> rowList = importValue.getContentRows();

        for (Import.Row row: rowList) {
            try {
                int f104Column = importValue.getFunctionPosition(FunctionConstants.F104);
                String bibId = row.getColumns().get(f104Column).getValue().toString();
                row.getColumns().add(new Import().new Column<>(fdid111, "http://hdl.handle.net/10079/bibid/" + bibId));
            } catch (Exception e) {
                logger.trace("Error={}", e.getMessage()); //ignore
            }
        }
        importValue.setContentRows(rowList);
        return importValue;
    }
}
