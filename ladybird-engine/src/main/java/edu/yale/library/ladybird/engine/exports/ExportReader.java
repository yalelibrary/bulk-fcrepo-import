package edu.yale.library.ladybird.engine.exports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.FdidMarcMappingUtil;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.imports.ImportJobCtx;
import edu.yale.library.ladybird.engine.imports.ImportSourceDataReader;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.LocalIdMarcValue;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Reads from import job tables
 */
public class ExportReader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO(); //TODO

    /**
     * Main method. Reads import tables (import job contents and import source) to construct data.
     * @return ImportJobCtx
     */
    public ImportJobCtx readRowsFromImportTables() {


        final ExportRequestEvent exportRequestEvent = ExportEngineQueue.getJob(); //from Queue
        final int importId = exportRequestEvent.getImportId();

        logger.debug("Read job from export engine queue, importId={}", importId);

        int numRowsToWrite = getExpectedNumRowsToWrite(importId);

        if (numRowsToWrite == 0) {
            logger.debug("No rows to write.");
            return ImportJobCtx.newInstance();
        }

        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();

        final List<LocalIdMarcValue> bibIdValueList = importSourceDataReader.toLocalIdMarcValue(importId);

        //Get all FieldConstant. Each should have a column in the output.
        final List<FieldConstant> globalFConstantsList = FieldConstantRules.getApplicationFieldConstants();

        //Write exhead
        final Row exheadRow = new ImportEntity().new Row();

        for (final FieldConstant fieldConst : globalFConstantsList) {
            ImportEntity importEntity = new ImportEntity();
            logger.trace("Adding to exhead={}", fieldConst.getName());
            exheadRow.getColumns().add(importEntity.new Column(fieldConst, fieldConst.getName()));
        }

        final List<Row> resultRowList = new ArrayList<>();

        resultRowList.add(exheadRow); //N.B. exhead row added

        //Get import job contents rows of columns. These will be MERGED with the oai data:

        final List<Row> regularRows = getImportJobContents(importId);

        ImportEntityValue importEntityValue = new ImportEntityValue(regularRows);

        int bibIdCol;
        try {
            bibIdCol = importEntityValue.getFunctionPosition(FunctionConstants.F104); //or F105 TODO
        } catch (Exception e) {
           logger.debug("Col with bibId data not found in this dataset(sheet)");
            bibIdCol = -1; //TODO
        }

        for (int i = 0; i < numRowsToWrite; i++) {
            final Row row = regularRows.get(i);
            final Row rowToWrite = new ImportEntity().new Row();
            final List<Column> cols = row.getColumns();

            // for each field constant (NOT for each column):

            for (final FieldConstant fieldConst : globalFConstantsList) {

                logger.trace("Eval={}", fieldConst.getName());

                final String regularValue = findColValueForThisFieldConstant(fieldConst, cols);

                String oaiValue = "";

                //merge only if bibId col exists, and if not a function constant (like f104 itself)
                if (bibIdCol != -1 && !FunctionConstants.isFunction(fieldConst.getName())) {

                    final Column<String> bibIdColumn = cols.get(bibIdCol);

                    //logger.debug("BibIdColumn={}", bibIdColumn.toString());

                    for (LocalIdMarcValue localIdMarcValue : bibIdValueList) { //iterate to find the right data..TODO utility method

                        //logger.debug("Comparing bibIdcolum={} with value={}", bibIdColumn.getValue(), localIdMarcValue.getBibId().getId());

                        if (localIdMarcValue.getBibId().getId().equals(bibIdColumn.getValue())) {
                            oaiValue = getMultiMapValue(fieldConst, localIdMarcValue.getValue());
                            //logger.debug("OAI value={}", oaiValue);
                        }
                    }
                }

                final String mergedValue = regularValue + oaiValue;
                //logger.debug("Merged value={} from regular value={}", mergedValue, regularValue);

                final ImportEntity importEntity = new ImportEntity();
                rowToWrite.getColumns().add(importEntity.new Column<>(fieldConst, mergedValue));
            }
            resultRowList.add(rowToWrite);
        }

        final ImportJobCtx importJobCtx = new ImportJobCtx();
        importJobCtx.setImportJobList(resultRowList);
        importJobCtx.setMonitor(exportRequestEvent.getMonitor());
        return importJobCtx;
    }

    /**
     * TODO has logic for only a single field. Following is an example of one field (245), and one subfield("a")
     * @param map
     * @return
     */
    public String getMultiMapValue(FieldConstant fieldConstant, final Multimap<Marc21Field, Map<String, String>> map) {
        logger.trace("Extracting value for={}", fieldConstant.getName());

        FdidMarcMappingUtil fdidMarcMappingUtil = new FdidMarcMappingUtil();
        fdidMarcMappingUtil.setFieldMarcMappingDAO(new FieldMarcMappingHibernateDAO()); //TODO

        final Marc21Field fieldToQuery = fdidMarcMappingUtil.toMarc21Field(fieldConstant);

        logger.trace("Field to query={}", fieldToQuery.toString());

        final Collection<Map<String, String>> attrCollection = map.get(fieldToQuery);

        logger.trace("Attrcollection size={}", attrCollection.size());

        final Iterator<Map<String, String>> it = attrCollection.iterator();
        String oaiValue = "";

        while (it.hasNext()) {
            final Map<String, String> attrValueMap = it.next();
            logger.trace("Attr value map={}", attrValueMap.toString());
            if (attrValueMap.get("a") != null) {
                oaiValue = attrValueMap.get("a");
            }
        }
        logger.trace("Found value={}", oaiValue);
        logger.trace("Map is={]", map.toString());
        logger.trace("Map value is={}", attrCollection.toString());
        return oaiValue;
    }


    /**
     * TODO test
     * Returns rows of ImportJobContents
     * @param importId The import id of the job
     * @return list of ImportEntity.Row
     */
    private List<Row> getImportJobContents(final int importId) {
        logger.trace("Getting Import Job contents");

        final List<Row> resultList = new ArrayList<>();

        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();

        List<ImportJobExhead> importJobExheads = new ArrayList<>();

        try {
            importJobExheads = importJobExheadDAO.findByImportId(importId);
        } catch (Exception e) {
            logger.error("Error={}", e);
        }

        logger.debug("ImportJobExheads size={}", importJobExheads.size());
        //logger.debug("ImportJobExheads content={}", importJobExheads.toString());

        //final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        int numRowsPerImportJob = importJobContentsDAO.getNumRowsPerImportJob(importId);
        numRowsPerImportJob = numRowsPerImportJob + 1;

        //logger.trace("Import job contents total rows={}", importJobContentsDAO.getNumEntriesPerImportJob(importId));
        logger.debug("Import job contents num rows={}", numRowsPerImportJob);

        for (int i = 0; i < numRowsPerImportJob; i++) {
            final List<ImportJobContents> rowJobContentsList = importJobContentsDAO.findByRow(importId, i); //note
            //logger.debug("Job contents for row={}" + rowJobContentsList.toString());
            final Row row = new ImportEntity().new Row();

            //Warning: Gets all columns (including F104/F105 COLUMN)
            for (int j = 0; j < rowJobContentsList.size(); j++) {
                //logger.debug("Processing row={}, col={}", i, j);
                try {
                    final ImportJobExhead importJobExhead = importJobExheads.get(j);
                    String headerValue = importJobExhead.getValue();
                    logger.trace("Header val={}", headerValue);
                    final FieldConstant fieldConstant = FieldConstantRules.convertStringToFieldConstant(headerValue);
                    if (fieldConstant == null) {
                        logger.trace("Field Constant null for headerValue={}", headerValue);
                    }
                    final ImportJobContents jobContents = rowJobContentsList.get(j);
                    logger.trace("JobContents={}", jobContents.toString());
                    row.getColumns().add(new ImportEntity().new Column<>(fieldConstant, jobContents.getValue()));
                    //logger.debug("Added value={}", jobContents.getValue());
                } catch (Exception e) {
                    //logger.error("Error retrieving value", e.getMessage());
                    continue;
                }
            }
            resultList.add(row);
        }
        return resultList;
    }

    /**
     * Return expected number of write by consutling the import job contents.
     * @param importId
     * @return number of exact rows or 0 if error
     */
    private int getExpectedNumRowsToWrite(final int importId) {
        int expectedNumRowsToWrite = 0;

        try {
            expectedNumRowsToWrite = importJobContentsDAO.getNumRowsPerImportJob(importId); //gets contents count not exhead
            expectedNumRowsToWrite = expectedNumRowsToWrite + 1; //N.B. to accomodate for row num. starting from 0
        } catch (Exception e) {
            logger.error("No rows found");
        }

        return expectedNumRowsToWrite;
    }

    /**
     * @see edu.yale.library.ladybird.engine.imports.ImportEntityValue for similar methods
     */
    private String findColValueForThisFieldConstant(final FieldConstant f, final List<Column> column) {
        for (final Column<String> col: column) {
            try {
                if (col.getField().getName().equals(f.getName())) {
                    return col.getValue();
                }
            } catch (Exception e) {
                logger.trace("Null value for={} or={}", f.toString(), column.toString());
            }
        }
        return "";
    }

}
