package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import edu.yale.library.ladybird.engine.model.FieldConstantUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads from import job tables and data structures.
 */
public class ExportReader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();

    final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();

    /**
     * Main method. Reads import tables (import job contents and import source) and merges OAI data
     * @return ImportEntityContext
     */
    public ImportEntityContext read() {
        final ExportRequestEvent exportRequestEvent = ExportEngineQueue.getJob(); //from Queue
        final int importId = exportRequestEvent.getImportId();
        final int numRowsToWrite = importJobContentsDAO.getNumRowsPerImportJob(importId) + 1;

        logger.debug("Read job={} from export engine queue. Expected num to write={}", importId, numRowsToWrite);

        if (numRowsToWrite <= 1) {
            logger.debug("No rows to write for importId={}!", importId);
            ImportEntityContext empty = ImportEntityContext.newInstance();
            empty.setImportId(importId);
            return empty;
        }

        //Get all FieldConstant. Each should have a column in the output.
        final List<FieldConstant> ladybirdFieldConstants = FieldConstantUtil.getApplicationFieldConstants();

        //Write exhead
        final Row exheadRow = new ImportEntity().new Row();

        for (final FieldConstant fieldConst : ladybirdFieldConstants) {
            ImportEntity importEntity = new ImportEntity();
            exheadRow.getColumns().add(importEntity.new Column(fieldConst, fieldConst.getName()));
        }

        final List<Row> resultRowList = new ArrayList<>();
        resultRowList.add(exheadRow); //N.B. exhead row added

        //Get import job contents rows of columns. These will be MERGED with the oai data:
        final List<Row> plainRows = new ImportWriterConverter().read(importId);
        logger.debug("Import job contents rows size={} for importId={}", plainRows.size(), importId);

        logger.debug("Merging with OAI provider values");
        ExportReaderOaiMerger exportReaderOaiMerger = new ExportReaderOaiMerger();
        int oaiColumnIndex = getLocalIdentifierColumnNum(plainRows);
        List<Row> contentRows = exportReaderOaiMerger.merge(importId, oaiColumnIndex, ladybirdFieldConstants, plainRows);
        resultRowList.addAll(contentRows);

        final ImportEntityContext iContext = new ImportEntityContext();
        iContext.setImportJobList(resultRowList);
        iContext.setMonitor(exportRequestEvent.getMonitor());
        iContext.setImportId(importId);
        return iContext;
    }


    /**
     * Gets F104/F105 position. Should probably be extracted?
     * @param regularRows List of Row
     * @return position
     */
    private int getLocalIdentifierColumnNum(List<Row> regularRows) {
        ImportEntityValue importEntityValue = new ImportEntityValue(regularRows);

        int index = -1;
        try {
            index = importEntityValue.getFunctionPosition(FunctionConstants.F104); //or F105 TODO
        } catch (Exception e) {
            logger.debug("Col with bib data not found in this dataset(sheet)");
        }

        if (index == -1) {
            try {
                index = importEntityValue.getFunctionPosition(FunctionConstants.F105); //or F105 TODO
            } catch (Exception e) {
                logger.debug("Col with barcode data not found in this dataset(sheet)");
            }
        }
        return index;
    }

    /**
     * Utility to convert db values into List<Row>
     */
    private class ImportWriterConverter {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        /**
         * Returns rows from import job tables
         * @param importId The import id of the job
         * @return list of ImportEntity.Row or empty list
         */
        public List<Row> read(final int importId) {
            logger.debug("Reading import rows for importId={}", importId);

            try {
                List<ImportJobExhead> exheads = importJobExheadDAO.findByImportId(importId);
                logger.trace("ImportJobExheads size={}", exheads.size());

                final int numRowsPerImportJob = importJobContentsDAO.getNumRowsPerImportJob(importId) + 1;
                final List<Row> importRows = new ArrayList<>(numRowsPerImportJob);

                //cache functions
                final Map<String, FieldConstant> cache = new HashMap<>();


                for (int i = 0; i < numRowsPerImportJob; i++) {
                    final List<ImportJobContents> rowJobContents = importJobContentsDAO.findByRow(importId, i);
                    final Row row = new ImportEntity().new Row();

                    if (i % 1000 == 0)  {
                        logger.debug("Read row={}", i);
                    }

                    //Warning: Gets all columns (including F104/F105 COLUMN)
                    for (int j = 0; j < rowJobContents.size(); j++) {
                        try {
                            final ImportJobExhead importJobExhead = exheads.get(j);
                            final String header = importJobExhead.getValue();
                            //logger.trace("Header={}", header);

                            FieldConstant fieldConstant;

                            if (cache.containsKey(header)) {
                                fieldConstant = cache.get(header);
                            } else {
                                fieldConstant = FieldConstantUtil.toFieldConstant(header);

                                if (fieldConstant == null) {
                                    logger.trace("Field Constant null for headerValue={}", header);
                                }

                                cache.put(header, fieldConstant);
                            }

                            final ImportJobContents jobContents = rowJobContents.get(j);
                            row.getColumns().add(new ImportEntity().new Column<>(fieldConstant, jobContents.getValue()));
                        } catch (Exception e) {
                            logger.error("Error retrieving value={}", e.getMessage());
                        }
                    }
                    importRows.add(row);
                }
                return importRows;
            } catch (Exception e) {
                logger.error("Error", e);
            }

            return Collections.emptyList();
        }

    }



}
