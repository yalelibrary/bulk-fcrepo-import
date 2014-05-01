package edu.yale.library.ladybird.engine.exports;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.FieldConstantRules;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;

import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

/**
 * Reads from import job tables
 */
public class ExportReader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Main method. Reads import tables (import job contents and import source) to construct data.
     * @return list of ImportEntity.Row
     */
    public List<Row> readRowsFromImportTables() {

        //Get the job from the queue
        final ExportRequestEvent exportRequestEvent = ExportEngineQueue.getJob();
        final int importId = exportRequestEvent.getImportId();

        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();

        final int expectedNumRowsToWrite = importJobContentsDAO.getNumRowsPerImportJob(importId);

        logger.debug("Num. import job content entries={} for jobId={}", expectedNumRowsToWrite, importId);

        Map<Integer, Multimap<Marc21Field, Map<String, String>>> bibIdDataFieldTypeMap = null;

        //Read all bibIds data
        bibIdDataFieldTypeMap = readBibIdData(importId, expectedNumRowsToWrite);

        //logger.debug("BibIdDataFieldTypeMap size={}", bibIdDataFieldTypeMap.size());
        //logger.debug("BibIdDataFieldTypeMap={}", bibIdDataFieldTypeMap.toString());

        //Get all FunctionConstants. Every FunctionConstant should have a column in the output spreadsheet.
        final List<FieldConstant> globalFConstantsList = FieldConstantRules.getApplicationFieldConstants();

        final List<Row> resultRowList = new ArrayList<>();

        //Write exhead
        final Row exheadRow = new ImportEntity().new Row();

        for (final FieldConstant fConst: globalFConstantsList) {
            ImportEntity importEntity = new ImportEntity();
            //logger.debug("Adding header={}", fConst.toString());
            exheadRow.getColumns().add(importEntity.new Column(fConst, fConst.getName()));
        }
        resultRowList.add(exheadRow);
        logger.debug("Header row col size={}", exheadRow.getColumns().size());

        //Get import job contents rows of columns. These will be merged with the oai data:
        final List<Row> regularRows = getImportJobContents(importId);

        for (int i = 0; i < expectedNumRowsToWrite; i++) {
            final Row row = regularRows.get(i);
            final Row rowToWrite = new ImportEntity().new Row();
            final List<Column> cols = row.getColumns();

            for (final FieldConstant fieldConst: globalFConstantsList) {

                //logger.debug("Writing FieldConstant={}", fieldConst.getName());

                final String nonOaiValue = findColValueForThisFieldConstant(fieldConst, cols);

                final Multimap<Marc21Field, Map<String, String>> map = bibIdDataFieldTypeMap.get(i);

                String oaiValue = "";

                if (bibIdDataFieldTypeMap.containsKey(i)) {
                    oaiValue = extractValue(fieldConst, bibIdDataFieldTypeMap.get(i));
                } else {
                    logger.debug("No oai value exists for this FieldConstant={}", fieldConst.getName());
                }

                final String mergedValue = nonOaiValue + oaiValue;
                //logger.debug("Merged value={}", mergedValue);

                final ImportEntity importEntity = new ImportEntity();
                rowToWrite.getColumns().add(importEntity.new Column<>(fieldConst, mergedValue));
            }
            resultRowList.add(rowToWrite);
        }
        return resultRowList;
    }

    /**
     * TODO has logic for only a single field
     * @param map
     * @return
     */
    private String extractValue(FieldConstant fieldConstant, final Multimap<Marc21Field, Map<String, String>> map) {

        //Following is an example of one field (245), and one subfield("a")
        final Marc21Field fieldToQuery = FieldConstantRules.getFieldConstantToMarc21Mapping(fieldConstant);

        final Collection<Map<String, String>> attrCollection = map.get(fieldToQuery);

        final Iterator<Map<String, String>> it = attrCollection.iterator();

        String oaiValue = "";

        while (it.hasNext()) {
            final Map<String, String> attrValueMap = it.next();
            if (attrValueMap.get("a") != null) {
                oaiValue = attrValueMap.get("a");
            }
        }
        return oaiValue;
    }


    /**
     * Populates Rows of ImportJobContents
     * @param importId The import id of the job
     * @return list of ImportEntity.Row
     */
    private List<Row> getImportJobContents(final int importId) {

        final List<Row> resultList = new ArrayList<>();

        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        final List<ImportJobExhead> importJobExheads = importJobExheadDAO.findByImportId(importId);

        logger.debug("ImportJobExheads size={}", importJobExheads.size());
        logger.debug("ImportJobExheads content={}", importJobExheads.toString());

        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        final int numRowsPerImportJob = importJobContentsDAO.getNumRowsPerImportJob(importId);

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
                    //logger.debug("Header val={}", headerValue);
                    final FieldConstant fieldConstant = FieldConstantRules.convertStringToFieldConstant(headerValue);
                    if (fieldConstant == null) {
                        logger.debug("Field Constant null for headerValue={}", headerValue);
                    }
                    final ImportJobContents jobContents = rowJobContentsList.get(j);
                    //logger.debug("JobContents={}", jobContents.toString());
                    row.getColumns().add(new ImportEntity()
                            .new Column<>(fieldConstant, jobContents.getValue()));
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
     * @see #getImportJobContents(int)
     * @param importId
     * @param expectedNumRowsToWrite
     * @return
     */
    private Map<Integer, Multimap<Marc21Field, Map<String, String>>> readBibIdData(final int importId,
                                                                                  final int expectedNumRowsToWrite) {
        logger.debug("Reading F104 or F105 data from import source table, and populating the value map");
        logger.debug("ImportId={}, ExpectedNumRowsToWrite={}", importId, expectedNumRowsToWrite);

        final ImportSourceDataDAO importSourceDataDAO = new ImportSourceDataHibernateDAO();

        //map(k,v), where k=rowNo, v=map(s,t), where s=Mar21Tag,t=Marc21DataFieldType
        final Map<Integer, Multimap<Marc21Field, Map<String, String>>> map = new HashMap<>();

        //Find all ImportSourceData entries pertaining to a particular row:
        for (int i = 0; i < expectedNumRowsToWrite; i++) {
            final List<ImportSourceData> importSourcesList = importSourceDataDAO.findByImportId(importId, i);
            //logger.debug("ImportSourceDataList={}", importSourcesList.toString());
            map.put(i, marshallMarcData(importSourcesList));
        }

        //logger.debug("All import source contents={}" + importSourceDataDAO.findAll().toString());
        return map;
    }

    /**
     * @see #readBibIdData(int, int)
     * @param importSourceDataList
     * @return
     */
    private Multimap<Marc21Field, Map<String, String>> marshallMarcData(final List<ImportSourceData> importSourceDataList) {

        //final Map<Marc21Field, DatafieldType> marcTagData = new HashMap<>();

        //populate map (e.g. (245,{a,"text"}), (245, {b,"text b"}). This map will then be read.
        final Multimap<Marc21Field, Map<String, String>> map = HashMultimap.create(); //k=tag, v={subfield,value}

        for (int i = 0; i < importSourceDataList.size(); i++) {
            final ImportSourceData entry = importSourceDataList.get(i);
            final String k1 = entry.getK1();

            switch(k1) {
                case "880":
                    //logger.debug("Ignoring field 880");
                    break;
                default:
                    //logger.debug("Putting field={} value={}", k1, entry.getValue());
                    final Map<String, String> attrValue = new HashMap<>();
                    attrValue.put(entry.getK2(), entry.getValue());
                    map.put(Marc21Field.valueOfTag(k1), attrValue);
                    break;
            }
        }
        return map;
    }

    /**
     * Returns the value in List of columns corresponding to a FieldConstant.
     * TODO Use exhead to read the col directly.
     * @param f
     * @param column
     * @return
     */
    private String findColValueForThisFieldConstant(final FieldConstant f, final List<Column> column) {
        for (final Column<String> col: column) {
            try {
                if (col.getField().getName().equals(f.getName())) {
                    //logger.debug("Col value={}", col.getValue());
                    return col.getValue();
                }
            } catch (Exception e) {
                logger.trace("Null value for={} or={}", f.toString(), column.toString());
            }
        }
        return "";
    }

}
