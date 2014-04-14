package edu.yale.library.ladybird.engine.exports;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.Marc21Field;
import edu.yale.library.ladybird.engine.oai.DatafieldType;
import edu.yale.library.ladybird.engine.oai.Record;
import edu.yale.library.ladybird.engine.oai.SubfieldType;
import edu.yale.library.ladybird.kernel.beans.ImportJobContents;
import edu.yale.library.ladybird.kernel.beans.ImportJobExhead;
import edu.yale.library.ladybird.kernel.beans.ImportSource;
import edu.yale.library.ladybird.kernel.beans.ImportSourceData;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.engine.cron.ExportEngineQueue;
import edu.yale.library.ladybird.engine.imports.ImportEntity;

import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Reads from import job tables
 */
public class ExportReader {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Main method. Reads import tables (import job contents and import source) to construct data.
     * @return
     */
    public List<ImportEntity.Row> readRowsFromImportTables() {

        //Get the job from the queue
        final ExportRequestEvent exportRequestEvent = ExportEngineQueue.getJob();
        final int importId = exportRequestEvent.getImportId();

        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();

        final int expectedNumRowsToWrite = importJobContentsDAO.getNumRowsPerImportJob(importId);

        logger.debug("Num. import job content entries={} for jobId={}", expectedNumRowsToWrite, importId);

        final ImportSourceDataDAO importSourceDataDAO = new ImportSourceDataHibernateDAO();

        Map<Integer, Multimap<Marc21Field, Map<String, String>>> bibIdDataFieldTypeMap = null;

            //Read all bibIds data
        bibIdDataFieldTypeMap = readBibIdData(importId, expectedNumRowsToWrite);

        logger.debug("BibIdDataFieldTypeMap size={}", bibIdDataFieldTypeMap.size());
        logger.debug("BibIdDataFieldTypeMap={}", bibIdDataFieldTypeMap.toString());


        //Get all FunctionConstants. Every FunctionConstant should have a column in the output spreadsheet.

        final List<FieldConstant> globalFConstantsList = getApplicationFieldConstants();


        final List<ImportEntity.Row> resultRowList = new ArrayList<>();

        //Write exhead

        final ImportEntity.Row exheadRow = new ImportEntity().new Row();


        for (final FieldConstant fConst: globalFConstantsList) {
            ImportEntity importEntity = new ImportEntity();
            logger.debug("Adding header={}", fConst.toString());
            exheadRow.getColumns().add(importEntity.new Column(fConst, fConst.getName()));
        }
        resultRowList.add(exheadRow);
        logger.debug("Header row col size={}", exheadRow.getColumns().size());

        //Get import job contents rows of columns. These will be merged with the oai data:
        final List<ImportEntity.Row> regularRows = marshallImportJobContents(importId);

        logger.debug("Done reading Import job contents");

        for (int i = 0; i < expectedNumRowsToWrite; i++) {
            final ImportEntity.Row row = regularRows.get(i);
            final ImportEntity.Row rowToWrite = new ImportEntity().new Row();

            final List<ImportEntity.Column> cols = row.getColumns();

            for (final FieldConstant fieldConst: globalFConstantsList) {

                logger.debug("Writing FieldConstant={}", fieldConst.getName());

                final String regularValue = findColValueForThisFieldConstant(fieldConst, cols);

                final Multimap<Marc21Field, Map<String, String>> map = bibIdDataFieldTypeMap.get(i);

                String oaiValue = "";

                if (bibIdDataFieldTypeMap.containsKey(i)) {
                    oaiValue = extractValue(fieldConst, bibIdDataFieldTypeMap.get(i));
                } else {
                    logger.debug("No oai value exists for this FieldConstant={}", fieldConst.getName());
                }

                final String mergedValue = regularValue + oaiValue;

                //Save this:
                final ImportEntity importEntity = new ImportEntity();

                //The end value:
                logger.debug("Merged value={}", mergedValue);

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
    public String extractValue(FieldConstant fieldConstant, final Multimap<Marc21Field, Map<String, String>> map) {

        //Following is an example of one field (245), and one subfield("a")

        Marc21Field fieldToQuery = getFieldConstantToMarc21Mapping(fieldConstant);

        final Collection<Map<String, String>> attrCollection = map.get(fieldToQuery);

        final Iterator<Map<String,String>> it = attrCollection.iterator();
        String oaiValue = "";

        while (it.hasNext()) {
            final Map<String,String> attrValueMap = it.next();
            if (attrValueMap.get("a") != null) {
                oaiValue = attrValueMap.get("a");
            }
        }
        return oaiValue;

    }

    /**
     * Mapping (rules) will be defined via db. Subject to removal
     * @param fieldConstant
     * @return
     */
    public Marc21Field getFieldConstantToMarc21Mapping(final FieldConstant fieldConstant) {
        //TODO simplify
        if (fieldConstant.getName().equals("70") || fieldConstant.getName().equals("Title{fdid=70}")) {
            return Marc21Field._245;
        }
        return Marc21Field.UNK;
    }

    /**
     * Populates Rows of ImportJobContents
     * @param importId The import id of hte job
     * @return list of ImportEntity.Row
     */
    private List<ImportEntity.Row> marshallImportJobContents(final int importId) {

        final List<ImportEntity.Row> resultList = new ArrayList<>();

        final ImportJobExheadDAO importJobExheadDAO = new ImportJobExheadHibernateDAO();
        final List<ImportJobExhead> importJobExheads = importJobExheadDAO.findByImportId(importId);

        logger.debug("ImportJobExheads size={}", importJobExheads.size());
        logger.debug("ImportJobExheads content={}", importJobExheads.toString());

        final ImportJobContentsDAO importJobContentsDAO = new ImportJobContentsHibernateDAO();
        final int numRowsPerImportJob = importJobContentsDAO.getNumRowsPerImportJob(importId);

        for (int i = 0; i < numRowsPerImportJob; i++) {
            final List<ImportJobContents> rowJobContentsList = importJobContentsDAO.findByRow(i);
            //logger.debug("Job contents for row={}" + rowJobContentsList.toString());
            final ImportEntity.Row row = new ImportEntity().new Row();

            //Warning: Gets all columns (including F104/F105 COLUMN)
            for (int j = 0; j < rowJobContentsList.size(); j++) {
                //logger.debug("Processing row={}, col={}", i, j);
                try {
                    final ImportJobExhead importJobExhead = importJobExheads.get(j);
                    String headerValue = importJobExhead.getValue();
                    logger.debug("Header val={}", headerValue);
                    final FieldConstant fieldConstant = convertStringToFieldConstant(headerValue);
                    if (fieldConstant == null) {
                        logger.debug("Field Constant null for headerValue={}", headerValue);
                    }
                    final ImportJobContents jobContents = rowJobContentsList.get(j);
                    logger.debug("JobContents={}", jobContents.toString());
                    row.getColumns().add(new ImportEntity()
                            .new Column<>(fieldConstant, jobContents.getValue()));
                    logger.debug("Added value={}", jobContents.getValue());
                } catch (Exception e) {
                    logger.error("Error retrieving value", e.getMessage());
                    continue;
                }
            }
            resultList.add(row);
        }
        return resultList;
    }


    public Map<Integer, Multimap<Marc21Field, Map<String, String>>> readBibIdData(final int importId,
                                                                                  final int expectedNumRowsToWrite) {
        logger.debug("Reading F104 or F105 data from import source table, and populating the value map");
        logger.debug("ImportId={}, ExpectedNumRowsToWrite={}", importId, expectedNumRowsToWrite);

        final ImportSourceDataDAO importSourceDataDAO = new ImportSourceDataHibernateDAO();

        //map(k,v), where k=rowNo, v=map(s,t), where s=Mar21Tag,t=Marc21DataFieldType
        final Map<Integer, Multimap<Marc21Field, Map<String, String>>> map = new HashMap<>();

        //Find all ImportSourceData entries pertaining to a particular row:
        for (int i = 0; i < expectedNumRowsToWrite; i++) {
            final List<ImportSourceData> importSourcesList = importSourceDataDAO.findByImportId(importId, i);
            logger.debug("ImportSourceDataList={}", importSourcesList.toString());
            map.put(i, marshallMarcData(importSourcesList));
        }

        logger.debug("All import source contents={}" + importSourceDataDAO.findAll().toString());
        return map;
    }

    public Multimap<Marc21Field, Map<String, String>> marshallMarcData(final List<ImportSourceData> importSourceDataList) {

        final Map<Marc21Field, DatafieldType> marcTagData = new HashMap<>();

        //populate map (e.g. (245,{a,"text"}), (245, {b,"text b"}). This map will then be read.
        final Multimap<Marc21Field, Map<String, String>> map = HashMultimap.create(); //k=tag, v={subfield,value}


        for (int i = 0; i < importSourceDataList.size(); i++) {
            final ImportSourceData entry = importSourceDataList.get(i);
            final String k1 = entry.getK1();

            switch(k1) {
                case "880":
                    logger.debug("Ignoring field 880");
                    break;
                default:
                    logger.debug("Putting field={}", k1);
                    final Map<String, String> attrValue = new HashMap<>();
                    attrValue.put(entry.getK2(), entry.getValue());
                    map.put(getMar21FieldForString(k1), attrValue);
                    break;
            }
        }
        return map;
    }

    /**
     * Returns the value in List of columns corresponding to a FieldConstant. TODO Use exhead to read the col directly.
     * @param f
     * @param column
     * @return
     */
    public String findColValueForThisFieldConstant(final FieldConstant f, final List<ImportEntity.Column> column) {
        for (final ImportEntity.Column<String> col: column) {
            try {
                if (col.getField().getName().equals(f.getName())) {
                    return col.getValue();
                }
            } catch (Exception e) {
                logger.error("Null value for={} or={}", f.toString(), column.toString());
                logger.error(e.getMessage());
            }
        }
        return "";
    }

    @Deprecated
    public Marc21Field stringToMarc21Field(String tag) {
        final String TAG_ID = "_"; //TODO
        return Marc21Field.valueOf(TAG_ID + tag);
    }

    @Deprecated
    public Marc21Field getMar21FieldForString(String tag) {
        final String TAG_ID = "_"; //TODO
        try {
            Marc21Field mf =  Marc21Field.valueOf(TAG_ID + tag);
            return mf;
        } catch (IllegalArgumentException e) {
            //ignore fields since need to fill the Mar21Field list
            logger.error(e.getMessage());
            return Marc21Field.UNK;
        }
    }

    /**
     * Converts string to a FieldConstant (fdid or a FunctionConstants)
     * @param value
     * @return
     */
    public FieldConstant convertStringToFieldConstant(final String value){
        final Map<String, FieldConstant> map =  FieldDefinitionValue.getFieldDefMap();
        logger.debug(map.toString());
        final FieldConstant val;
        try {
            val = map.get(value);
            logger.debug("Found val={}", val.toString());
            return val;
        } catch (Exception e) {
            logger.error("Error converting to FieldConstant(FieldDefinition) value={}",value);
        }

        //See if it's a function constant
        try {
            final FunctionConstants f  = FunctionConstants.valueOf(value);
            return f;
        } catch (Exception e) {
            logger.error("Error converting to FieldConstant(FunctionConstant) value={}", value);
        }

        return null;
    }

    /**
     * Gets all FieldConstants (fdids etc) pertaining to the application
     * @return list of field constants
     */
    public List<FieldConstant> getApplicationFieldConstants() {

        final List<FieldConstant> globalFunctionConstants = new ArrayList<>();

        //add fdids
        final Map<String, FieldConstant> fdidMap = FieldDefinitionValue.getFieldDefMap();

        if (fdidMap.size() == 0) {
            logger.error("FieldDefMap size numero 0");
        }

        final Set<String> keySet = fdidMap.keySet();
        for (final String s: keySet) {
            final FieldConstant f = fdidMap.get(s);
            globalFunctionConstants.add(f);
        }

        //add F1, F104 etc
        final FunctionConstants[] functionConstants = FunctionConstants.values();

        for (final FieldConstant f: functionConstants) {
            globalFunctionConstants.add(f);
        }

        logger.debug("App FieldConstant size={}", globalFunctionConstants.size());
        return globalFunctionConstants;
    }


}
