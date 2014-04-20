package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class MediaFunctionProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void process(final List<ImportEntity.Row> rowList) {
        logger.debug("Processing list size={}", rowList.size());
        final ImportEntity.Row firstRow = rowList.get(0);
        final short columnWithImageField = findColumn(Collections.singletonList(firstRow), FunctionConstants.F3);
        logger.debug("Column with image field={}", columnWithImageField);
        final short columnWithF1Field = findColumn(Collections.singletonList(firstRow), FunctionConstants.F1);
        logger.debug("Column with F1={}", columnWithF1Field);
    }

    /**
     * @see ImportWriter#findColumn(java.util.List, edu.yale.library.ladybird.engine.model.FieldConstant)
     * @param rowList
     * @param f
     * @return
     */
    @Deprecated
    public short findColumn(final List<ImportEntity.Row> rowList, final FieldConstant f) {
        short order = 0;
        for (final ImportEntity.Row row: rowList) {
            final List<ImportEntity.Column> columns = row.getColumns();
            for (final ImportEntity.Column<String> col: columns) {
                if (col.getField().getName().equals(f.getName())) {
                    return order;
                }
                order++;
            }
        }
        return -1;
    }
}
