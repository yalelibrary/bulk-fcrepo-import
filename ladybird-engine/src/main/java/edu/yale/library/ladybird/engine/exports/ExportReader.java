package edu.yale.library.ladybird.engine.exports;

import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.engine.imports.ImportJobCtx;
import edu.yale.library.ladybird.engine.imports.ImportSourceDataReader;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
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
        logger.debug("Reading rows from Import tables");

        //Get the job from the queue
        final ExportRequestEvent exportRequestEvent = ExportEngineQueue.getJob();
        final int importId = exportRequestEvent.getImportId();


        int expectedNumRowsToWrite = importJobContentsDAO.getNumRowsPerImportJob(importId); //gets contents count not exhead
        expectedNumRowsToWrite = expectedNumRowsToWrite + 1; //N.B. to accomodate for row num. starting from 0

        logger.debug("Import job contents num rows={}", expectedNumRowsToWrite);
        logger.trace("Import job contents total rows={}", importJobContentsDAO.getNumEntriesPerImportJob(importId));

        ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();


        final Map<Integer, Multimap<Marc21Field, Map<String, String>>> bibIdDataFieldTypeMap
                = importSourceDataReader.readBibIdData(importId, expectedNumRowsToWrite);

        logger.debug("BibIdDataFieldTypeMap size={}", bibIdDataFieldTypeMap.size());
        //logger.debug("BibIdDataFieldTypeMap={}", bibIdDataFieldTypeMap.toString());

        //Get all FunctionConstants. Every FunctionConstant should have a column in the output spreadsheet.
        final List<FieldConstant> globalFConstantsList = FieldConstantRules.getApplicationFieldConstants();

        final List<Row> resultRowList = new ArrayList<>();

        //Write exhead
        final Row exheadRow = new ImportEntity().new Row();

        for (final FieldConstant fConst : globalFConstantsList) {
            ImportEntity importEntity = new ImportEntity();
            //logger.debug("Adding header={}", fConst.toString());
            exheadRow.getColumns().add(importEntity.new Column(fConst, fConst.getName()));
        }

        resultRowList.add(exheadRow); //N.B. exhead row is added

        //Get import job contents rows of columns. These will be merged with the oai data:
        final List<Row> regularRows = getImportJobContents(importId);

        for (int i = 0; i < expectedNumRowsToWrite; i++) {
            final Row row = regularRows.get(i);
            final Row rowToWrite = new ImportEntity().new Row();
            final List<Column> cols = row.getColumns();

            for (final FieldConstant fieldConst : globalFConstantsList) {

                //logger.debug("Writing FieldConstant={}", fieldConst.getName());

                final String nonOaiValue = findColValueForThisFieldConstant(fieldConst, cols);

                String oaiValue = "";

                if (bibIdDataFieldTypeMap.containsKey(i)) {
                    oaiValue = getMultiMapValue(fieldConst, bibIdDataFieldTypeMap.get(i));
                } else {
                    logger.debug("No OAI value exists for this FieldConstant={}", fieldConst.getName());
                }

                final String mergedValue = nonOaiValue + oaiValue;
                logger.debug("Merged value={} with non-Oaivalue={}", mergedValue, nonOaiValue);

                final ImportEntity importEntity = new ImportEntity();
                rowToWrite.getColumns().add(importEntity.new Column<>(fieldConst, mergedValue));
            }
            resultRowList.add(rowToWrite);
        }

        logger.debug("ImportJobCtx row size={}", resultRowList.size());

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
        logger.debug("Extracting value for={}", fieldConstant.getName());

        final Marc21Field fieldToQuery = FieldConstantRules.getFieldConstantToMarc21Mapping(fieldConstant);

        logger.debug("Field to query={}", fieldToQuery.toString());

        final Collection<Map<String, String>> attrCollection = map.get(fieldToQuery);

        logger.debug("Attr collection size={}", attrCollection.size());

        final Iterator<Map<String, String>> it = attrCollection.iterator();
        String oaiValue = "";

        while (it.hasNext()) {
            final Map<String, String> attrValueMap = it.next();
            logger.debug("Attr value map={}", attrValueMap.toString());
            if (attrValueMap.get("a") != null) {
                oaiValue = attrValueMap.get("a");
            }
        }
        logger.debug("Found value={}", oaiValue);
        logger.debug("Map is={]", map.toString());
        logger.debug("Map value is={}", attrCollection.toString());
        return oaiValue;
    }


    /**
     * Returns rows of ImportJobContents
     * @param importId The import id of the job
     * @return list of ImportEntity.Row
     */
    private List<Row> getImportJobContents(final int importId) {
        logger.debug("Getting Import Job contents");

        final List<Row> resultList = new ArrayList<>();

        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        final List<ImportJobExhead> importJobExheads = importJobExheadDAO.findByImportId(importId);

        logger.debug("ImportJobExheads size={}", importJobExheads.size());
        //logger.debug("ImportJobExheads content={}", importJobExheads.toString());

        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        int numRowsPerImportJob = importJobContentsDAO.getNumRowsPerImportJob(importId);
        numRowsPerImportJob = numRowsPerImportJob + 1;

        //logger.trace("Import job contents total rows={}", importJobContentsDAO.getNumEntriesPerImportJob(importId));
        logger.debug("Import job contents num rows={}", numRowsPerImportJob);

        for (int i = 0; i < numRowsPerImportJob; i++) {
            final List<ImportJobContents> rowJobContentsList = importJobContentsDAO.findByRow(i);
            //logger.debug("Job contents for row={}" + rowJobContentsList.toString());
            final Row row = new ImportEntity().new Row();

            //Warning: Gets all columns (including F104/F105 COLUMN)
            for (int j = 0; j < rowJobContentsList.size(); j++) {
                //logger.debug("Processing row={}, col={}", i, j);
                try {
                    final ImportJobExhead importJobExhead = importJobExheads.get(j);
                    String headerValue = importJobExhead.getValue();
                    logger.debug("Header val={}", headerValue);
                    final FieldConstant fieldConstant = FieldConstantRules.convertStringToFieldConstant(headerValue);
                    if (fieldConstant == null) {
                        logger.debug("Field Constant null for headerValue={}", headerValue);
                    }
                    final ImportJobContents jobContents = rowJobContentsList.get(j);
                    logger.debug("JobContents={}", jobContents.toString());
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
