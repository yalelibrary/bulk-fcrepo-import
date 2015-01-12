package edu.yale.library.ladybird.engine.exports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.ImportValue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.LocalIdMarcValue;
import edu.yale.library.ladybird.engine.oai.FdidMarcMappingUtil;
import edu.yale.library.ladybird.engine.oai.ImportSourceDataReader;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility for merging with OAI values
 */
public class ImportContextReaderOaiMerger {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    final ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();

    public List<Import.Row> merge(int importId, int localIdentifierColumnNum,
                                        List<FieldConstant> globalFConstantsList, List<Import.Row> plainRows) {

        int numRowsToWrite = getExpectedNumRowsToWrite(importId) + 1;

        logger.debug("Read job={} from export engine queue. Expected num to write={}", importId, numRowsToWrite);

        if (numRowsToWrite <= 1) {
            logger.error("No rows to merge!");
            Collections.emptyList();
        }

        List<Import.Row> resultRowList = new ArrayList<>();
        final List<LocalIdMarcValue> bibIdValueList = importSourceDataReader.readImportSourceData(importId);
        logger.debug("BibIdValueList size={} for importId={}", bibIdValueList.size(), importId);

        for (int i = 0; i < numRowsToWrite; i++) {
            final List<Import.Column> cols = plainRows.get(i).getColumns();
            final Import.Row rowToWrite = new Import().new Row();

            // for each field constant (NOT for each column):
            for (final FieldConstant fieldConst : globalFConstantsList) {
                final String mergedValue = mergeValue(fieldConst, localIdentifierColumnNum, cols, bibIdValueList);
                rowToWrite.getColumns().add(new Import().new Column<>(fieldConst, mergedValue));
            }
            resultRowList.add(rowToWrite);
        }
        return resultRowList;
    }

    public String mergeValue(FieldConstant fieldConst, int localIdentifierColumnNum, List<Import.Column> cols,
                        List<LocalIdMarcValue> bibIdValueList) {
        String plain =  ImportValue.findColValueFromRow(fieldConst, cols);

        if (plain == null || plain.isEmpty()) {
            plain = "";
        }

        String oai = "";

        //merge only if bibId col exists, and if not a function constant (like f104 itself)
        //TODO chk via fdid marc:
        if (localIdentifierColumnNum != -1 && !FunctionConstants.isFunction(fieldConst.getName())
                && !fieldConst.getTitle().equalsIgnoreCase("Handle")) {
            final Import.Column<String> bibIdColumn = cols.get(localIdentifierColumnNum);
            //logger.trace("bibIdcolumn={}", bibIdColumn);

            LocalIdMarcValue localIdMarcValue = LocalIdMarcValue.findMatch(bibIdValueList, bibIdColumn.getValue());
            //logger.trace("localIdMarcValue={}", localIdMarcValue);

            if (localIdMarcValue == null) {
                return oai;
            }

            oai = getMultimapMarc21Field(new FdidMarcMappingUtil().toMarc21Field(fieldConst), localIdMarcValue.getValueMap());
        }

        final String merged = plain + oai;
        logger.trace("FieldConstant={} Values: merged={} oai={} original={}", fieldConst.getName(), merged, oai, plain);
        return merged;
    }

    /**
     * Note: Logic for only one subfield("a")
     * @param multimap Map containing all Marc21Field values
     * @param marc21Field field to query
     * @return string value
     */
    public String getMultimapMarc21Field(Marc21Field marc21Field, Multimap<Marc21Field, Map<String, String>> multimap) {
        if (multimap.isEmpty()) {
            logger.debug("Empty multimap. Returning blank value.");
            return "";
        }

        Collection<Map<String, String>> attrCollection = multimap.get(marc21Field);
        Iterator<Map<String, String>> it = attrCollection.iterator();
        String val = "";

        while (it.hasNext()) {
            Map<String, String> attrValueMap = it.next();
            if (attrValueMap.get("a") != null) {
                val = attrValueMap.get("a");
            }
        }
        logger.trace("Found for field={} value={} map={} map value={} attr. collection size={}",
                marc21Field.toString(), val, multimap.toString(), attrCollection.toString(), attrCollection.size());
        return val;
    }

    /**
     * TODO lock numRows for a particular job
     */
    private int getExpectedNumRowsToWrite(final int importId) {
        try {
            return new ImportJobContentsHibernateDAO().getNumRowsPerImportJob(importId);
        } catch (Exception e) {
            throw e;
        }
    }


}
