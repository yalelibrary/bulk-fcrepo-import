package edu.yale.library.ladybird.engine.imports;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.DatafieldType;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.oai.MarcReadingException;
import edu.yale.library.ladybird.engine.oai.OaiHttpClient;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.engine.oai.Record;
import edu.yale.library.ladybird.engine.oai.SubfieldType;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.ImportJobBuilder;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobContentsBuilder;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.entity.ImportJobExheadBuilder;
import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.entity.ImportSourceDataBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;


/**
 *
 */
public class ImportWriter {
    private final Logger logger = getLogger(this.getClass());
    private final Date JOB_EXEC_DATE = new Date(System.currentTimeMillis()); //TODO

    private OaiProvider oaiProvider; //TODO

    private MediaFunctionProcessor mediaFunctionProcessor; //TODO

    /**
     * Full cycle import writing
     *
     * @param importEntityValue Abstraction on top of List<ImportEntity.Row>
     * @param ctx Job Context
     * @return Import Id
     */
    public int write(final ImportEntityValue importEntityValue, final ImportJobContext ctx) {
        final int importId = writeImportJob(ctx);
        //header
        writeExHead(importId, importEntityValue.getHeaderRow().getColumns());
        //contents
        writeContents(importId, importEntityValue); //change to only columns

        return importId;
    }

    /**
     * Write header ('ex head')
     *
     * @param importId
     * @param list
     */
    public void writeExHead(final int importId, final List<ImportEntity.Column> list) {
        logger.debug("Writing spreadsheet headers. Columns list size={}", list.size());

        final ImportJobExheadDAO dao = new ImportJobExheadHibernateDAO();
        int col = 0;

        for (final ImportEntity.Column column : list) {
            ImportJobExhead entry = new ImportJobExheadBuilder().setImportId(importId).setCol(col).
                    setDate(JOB_EXEC_DATE).setValue(column.field.getName()).createImportJobExhead();
            dao.save(entry);
            logger.debug("Saved={}", entry.toString());
            col++;
        }
    }

    /**
     * Writes content body
     *
     * @param importId
     * @param importEntityValue
     */
    @SuppressWarnings("unchecked")
    public void writeContents(final int importId, final ImportEntityValue importEntityValue) {
        List<ImportEntity.Row> rowList = importEntityValue.getContentRows();
        logger.debug("Writing spreadsheet body contents. Row list size={}", rowList.size());

        final ImportJobContentsDAO dao = new ImportJobContentsHibernateDAO();

        final List<FieldMarcMapping> fieldMarcMappingList = new FieldMarcMappingHibernateDAO().findAll();
        final Map<Marc21Field, FieldMarcMapping> marc21FieldMap = new HashMap<>(); //e.g. 880 -> FieldMarcMapping
        for (final FieldMarcMapping f : fieldMarcMappingList) {
            try {
                marc21FieldMap.put(Marc21Field.valueOf("_" + f.getK1()), f);
            } catch (IllegalArgumentException e) {  //No matching enum
                logger.debug("No such enum={}", f.getK1());
                continue;
            }
        }

        //logger.debug("Marc21 field map is={}", marc21FieldMap.toString());

        //Find the column number of the spreadsheet with F104 or F105 column.
        //final ImportEntity.Row firstRow = rowList.get(0);

        //final short columnWithOAIField = findColumn(Collections.singletonList(firstRow), FunctionConstants.F104);

        if (importEntityValue.fieldConstantsInExhead(FunctionConstants.F104)) {
            final int columnWithOAIField = importEntityValue.getFunctionPosition(FunctionConstants.F104);
            logger.debug("Column with oai field={}", columnWithOAIField);
        }

        //Get all the bibIds from this column
        final List<ImportEntity.Column> bibIdColumn = importEntityValue.getColumnValues(FunctionConstants.F104);
        final List<String> bibIds = new ArrayList<>();

        logger.debug("bibIdColumn size={}", bibIdColumn.size());

        for (ImportEntity.Column c : bibIdColumn) {
            bibIds.add(c.getValue().toString());
        }

        logger.debug("bibIds size={}", bibIds.size());

        final Map<String, Multimap<Marc21Field, ImportSourceData>> bibIdMarcValues =
                readBibIdMarcData(bibIds, marc21FieldMap, importId);
        logger.debug("bibIdMarcValues size={}", bibIdMarcValues.size());

        //Save to import_source_data:
        persistMarcData(bibIdMarcValues, importId);

        //Process F1
        if (importEntityValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            final int columnWithF1Field = importEntityValue.getFunctionPosition(FunctionConstants.F1);
            logger.debug("(flag) Column with F1={}", columnWithF1Field);
        }

        //Process F3 without F1 (to keep tests working)
        if (importEntityValue.fieldConstantsInExhead(FunctionConstants.F3) && !importEntityValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            final int f3ColumnNum = importEntityValue.getFunctionPosition(FunctionConstants.F3);
            logger.debug("Column with F3={}", f3ColumnNum);
            mediaFunctionProcessor.process(rowList, f3ColumnNum);
            logger.debug("Done media processing.");
        }

        //Process F3 with F1
        if (importEntityValue.fieldConstantsInExhead(FunctionConstants.F3) && importEntityValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            final int f3ColumnNum = importEntityValue.getFunctionPosition(FunctionConstants.F3);
            final int f1columnNum = importEntityValue.getFunctionPosition(FunctionConstants.F1);
            mediaFunctionProcessor.process(importId, rowList, f3ColumnNum, f1columnNum);
            logger.debug("Done media processing.");
        }

        //Save all columns to import_job_contents:
        for (int i = 0; i < rowList.size(); i++) {
            final ImportEntity.Row row = rowList.get(i);
            final List<ImportEntity.Column> columnsList = row.getColumns();

            //Persist all columns. Note that the f104/f105 column is also persisted.
            for (int j = 0; j < columnsList.size(); j++) {
                final ImportEntity.Column<String> col = columnsList.get(j);
                //logger.debug("Row={}, Col={}, Val={}", i, j, col.getField().getName());
                final ImportJobContents imjContentsEntry = new ImportJobContentsBuilder()
                        .setImportId(importId).setDate(JOB_EXEC_DATE).
                                setCol(j).setRow(i).setValue(col.getValue()).build();
                dao.save(imjContentsEntry);
                //logger.debug("Saved Import Job Contents entry={}", imjContentsEntry.toString());
            }
        }
    }

    /**
     * @param bibIdValueMap
     * @param importId
     */
    public void persistMarcData(final Map<String, Multimap<Marc21Field, ImportSourceData>> bibIdValueMap,
                                final int importId) {
        final ImportSourceDataDAO dao = new ImportSourceDataHibernateDAO();

        //TODO remove
        if (bibIdValueMap.size() == 0) {
            logger.debug("Empty bibIdValueMap");
            return;
        }

        //Read values:
        final Set<String> bibIds = bibIdValueMap.keySet();

        for (final String id : bibIds) {
            final Multimap<Marc21Field, ImportSourceData> m = bibIdValueMap.get(id);

            final Set<Marc21Field> marc21FieldsKeySet = m.keySet();

            for (final Marc21Field marc21Field : marc21FieldsKeySet) {
                //final ImportSourceData importSourceEntry = m.get(marc21Field);
                final Collection<ImportSourceData> importSourceDataList = m.get(marc21Field);

                final Iterator<ImportSourceData> it = importSourceDataList.iterator();

                while (it.hasNext()) {
                    ImportSourceData importSourceEntry = it.next();
                    dao.save(importSourceEntry);
                }
            }
        }
    }

    /**
     * Hits OAI feed and gets a Record
     *
     * @param bibIds
     * @param marc21FieldMap
     * @return
     */
    public Map<String, Multimap<Marc21Field, ImportSourceData>> readBibIdMarcData(final List<String> bibIds,
                                                                                  final Map<Marc21Field, FieldMarcMapping> marc21FieldMap, final int importId) {
        logger.debug("Reading marc data for the bibIds");

        final Map<String, Multimap<Marc21Field, ImportSourceData>> bibIdMarcValues = new HashMap<>();
          /* Read marc values for all these bibids */
        for (final String id : bibIds) {
            final OaiHttpClient oaiClient = new OaiHttpClient(oaiProvider);
            try {
                //logger.debug("Reading bibId={}", id);

                final Record recordForBibId = oaiClient.readMarc(id); //Read OAI feed for a specific barcode or bibid
                final Multimap<Marc21Field, ImportSourceData> marc21Values = populateMarcData(recordForBibId, importId);
                //FIXME passing map eachtime

                //logger.debug("Marc for bibId={} equals={}", id, marc21Values.toString());

                bibIdMarcValues.put(id, marc21Values);
            } catch (IOException e) {
                logger.error("Error reading source", e);
                e.printStackTrace();
            } catch (MarcReadingException e) {
                logger.error("Error reading oai marc record", e);
                e.printStackTrace();
            }
        }
        return bibIdMarcValues;
    }

    /**
     * @param record MarcRecord
     * @return a map(k,v) where k=Tag, v=ImportSourceData
     */
    private Multimap<Marc21Field, ImportSourceData> populateMarcData(final Record record, final int importId) {
        final List<DatafieldType> datafieldTypeList = record.getDatafield();
        final Multimap<Marc21Field, ImportSourceData> attrMap = HashMultimap.create();

        for (final DatafieldType type : datafieldTypeList) {
            final String tag = type.getTag();

            //Get subfields:
            final List<SubfieldType> subfieldTypeList = type.getSubfield();

            //Get k2 values
            for (final SubfieldType s : subfieldTypeList) {
                final String code = s.getCode(); //e.g "a", "c", "d"
                final String codeValue = s.getValue(); //e.g. "(oOCoLC) ocn709288147"

                final ImportSourceData importSourceData =
                        new ImportSourceDataBuilder().setK1(tag).setK2(code).setValue(codeValue).
                                setZindex(0).setDate(JOB_EXEC_DATE).setImportSourceId(importId)
                                .createImportSourceData(); //FIXME zindex, date
                attrMap.put(getMar21FieldForString(tag), importSourceData);
            }
        }
        return attrMap;
    }

    /**
     * @param tag
     * @return
     */
    public Marc21Field getMar21FieldForString(final String tag) {
        final String TAG_ID = "_"; //TODO
        try {
            Marc21Field marc21Field = Marc21Field.valueOf(TAG_ID + tag);
            return marc21Field;
        } catch (IllegalArgumentException e) {
            //ignore fields since need to fill the Mar21Field list
            logger.error(e.getMessage());
            return Marc21Field.UNK;
        }
    }

    /**
     * Writes import job
     *
     * @param ctx Context containing information about the job
     * @return newly minted job id
     */
    public Integer writeImportJob(final ImportJobContext ctx) {
        ImportJobDAO importJobDAO = new ImportJobHibernateDAO();
        return importJobDAO.save(new ImportJobBuilder().setDate(JOB_EXEC_DATE).setJobDirectory(ctx.getJobDir()).
                setJobFile(ctx.getJobFile()).setUserId(ctx.getUserId()).createImportJob());
    }

    /**
     * Sets OAI Provider. Subject to removal.
     *
     * @param oaiProvider The default OaiProvider
     */
    public void setOaiProvider(final OaiProvider oaiProvider) {
        this.oaiProvider = oaiProvider;
    }

    /**
     * Sets Media Function Processor. Subject to removal
     */
    public void setMediaFunctionProcessor(final MediaFunctionProcessor mediaFunctionProcessor) {
        this.mediaFunctionProcessor = mediaFunctionProcessor;
    }

}
