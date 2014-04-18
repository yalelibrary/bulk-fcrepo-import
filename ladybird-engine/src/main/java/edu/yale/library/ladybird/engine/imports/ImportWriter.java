package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.model.Marc21Field;
import edu.yale.library.ladybird.engine.oai.OaiHttpClient;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.engine.oai.Record;
import edu.yale.library.ladybird.engine.oai.SubfieldType;
import edu.yale.library.ladybird.engine.oai.DatafieldType;
import edu.yale.library.ladybird.kernel.beans.FieldMarcMapping;
import edu.yale.library.ladybird.kernel.beans.ImportJobContents;
import edu.yale.library.ladybird.kernel.beans.ImportJobExhead;
import edu.yale.library.ladybird.kernel.beans.ImportJobExheadBuilder;
import edu.yale.library.ladybird.kernel.beans.ImportJobBuilder;
import edu.yale.library.ladybird.kernel.beans.ImportJobContentsBuilder;
import edu.yale.library.ladybird.kernel.beans.ImportSourceData;
import edu.yale.library.ladybird.kernel.beans.ImportSourceDataBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.*;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.MarcReadingException;
import org.slf4j.Logger;

import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;


import java.io.IOException;
import java.util.*;

import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;


/**
 *
 */
public class ImportWriter {
    private final Logger logger = getLogger(this.getClass());

    private final Date JOB_EXEC_DATE = new Date(System.currentTimeMillis()); //TODO
    private OaiProvider oaiProvider; //TODO

    /**
     * Full cycle import writing.
     *
     * @param list list containing rows of data
     * @param ctx  import job context (contains information about the job)
     */
    public int write(final List<ImportEntity.Row> list, final ImportJobContext ctx) {
        final int importId = writeImportJob(ctx);
        //header
        writeExHead(importId, getHeaderRow(unmodifiableList(list)).getColumns());
        //contents
        writeContents(importId, getContentRows(unmodifiableList(list)));

        return importId;
    }

    /**
     * Write header
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
            logger.debug("Saved Exhead entry={}" + entry.toString());
            col++;
        }
    }

    /**
     * Writes content body
     *
     * @param importId
     * @param rowList
     */
    @SuppressWarnings("unchecked")
    public void writeContents(final int importId, final List<ImportEntity.Row> rowList) {
        logger.debug("Writing spreadsheet body contents. Row list size={}", rowList.size());

        final ImportJobContentsDAO dao = new ImportJobContentsHibernateDAO();

        final List<FieldMarcMapping> fieldMarcMappingList = new FieldMarcMappingHibernateDAO().findAll();
        final Map<Marc21Field, FieldMarcMapping> marc21FieldMap = new HashMap<>(); //e.g. 880 -> FieldMarcMapping
        for (final FieldMarcMapping f: fieldMarcMappingList) {
            try {
                marc21FieldMap.put(Marc21Field.valueOf("_" + f.getK1()), f);
            } catch (IllegalArgumentException e) {  //No matching enum
               logger.debug("No such enum={}", f.getK1());
               continue;
            }
        }

        //logger.debug("Marc21 field map is={}", marc21FieldMap.toString());

        //Find the column number of the spreadsheet with F104 or F105 column.
        //TODO move this to some exhead method
        final short columnWithOAIField = findBibIdColumn(Collections.singletonList(rowList.get(0)));
        logger.debug("Column with oai field={}", columnWithOAIField);

        //Get all the bibIds from this column
        final List<String> bibIds = readBibIdsFromColumn(rowList, columnWithOAIField);
        logger.debug("bibIds size={}", bibIds.size());

        final Map<String, Multimap<Marc21Field, ImportSourceData>> bibIdMarcValues = readBibIdMarcData(bibIds, marc21FieldMap, importId);
        logger.debug("bibIdMarcValues size={}", bibIdMarcValues.size());

        //Save to import_source_data:
        persistMarcData(bibIdMarcValues, importId);

        final short columnWithImageField = findImageColumn(Collections.singletonList(rowList.get(0)));
        logger.debug("Column with image field={}", columnWithImageField);


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

    public void persistMarcData(final Map<String, Multimap<Marc21Field, ImportSourceData>> bibIdValueMap,
                                final int importId) {
        final ImportSourceDataDAO dao = new ImportSourceDataHibernateDAO();

        //TODO remove
        if (bibIdValueMap.size() == 0) {
            logger.debug("Empty bibIdValueMap");
            return;
        }

        //Read values:
        final Set<String> bibIds =  bibIdValueMap.keySet();

        for (final String id: bibIds) {
            final Multimap<Marc21Field, ImportSourceData> m  = bibIdValueMap.get(id);

            final Set<Marc21Field> marc21FieldsKeySet = m.keySet();

            for (final Marc21Field marc21Field: marc21FieldsKeySet) {
                //final ImportSourceData importSourceEntry = m.get(marc21Field);
                final Collection<ImportSourceData> importSourceDataList = m.get(marc21Field);

                final Iterator<ImportSourceData> it = importSourceDataList.iterator();

                while (it.hasNext()) {
                    ImportSourceData importSourceEntry = it.next();
                    //logger.debug("Saving entry={}", importSourceEntry.toString());
                    dao.save(importSourceEntry);
                }
            }
        }
    }

    /**
     * Hits OAI feed and gets a Record
     * @param bibIds
     * @param marc21FieldMap
     * @return
     */
    public Map<String, Multimap<Marc21Field, ImportSourceData>> readBibIdMarcData(final List<String> bibIds,
                                                                   final Map<Marc21Field, FieldMarcMapping> marc21FieldMap,
                                                                   final int importId) {
        logger.debug("Reading marc data for the bibIds");

        final Map<String, Multimap<Marc21Field, ImportSourceData>> bibIdMarcValues = new HashMap<>();
          /* Read marc values for all these bibids */
        for (final String id: bibIds) {
            final OaiHttpClient oaiClient = new OaiHttpClient(oaiProvider);
            try {
                //logger.debug("Reading bibId={}", id);

                final Record recordForBibId = oaiClient.readMarc(id); //Read OAI feed for a specific barcode or bibid
                final Multimap<Marc21Field, ImportSourceData> marc21Values = populateMarcData(recordForBibId, importId); //FIXME passing map eachtime

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
     *
     * @param record
     * @return a map(k,v) where k=Tag, v=ImportSourceData
     */
    private Multimap<Marc21Field, ImportSourceData> populateMarcData(final Record record, final int importId) {
        final List<DatafieldType> datafieldTypeList = record.getDatafield();
        final Multimap<Marc21Field, ImportSourceData> attrMap = HashMultimap.create();

        for (final DatafieldType type: datafieldTypeList) {
            final String tag = type.getTag();

            //Get subfields:
            final List<SubfieldType> subfieldTypeList = type.getSubfield();

            //Get k2 values
            for (final SubfieldType s: subfieldTypeList) {
                final String code = s.getCode(); //e.g "a", "c", "d"
                final String codeValue = s.getValue(); //e.g. "(oOCoLC) ocn709288147"

                //Populate ImportSourceData:
                final ImportSourceData importSourceData =
                        new ImportSourceDataBuilder().setK1(tag).setK2(code).setValue(codeValue).
                                setZindex(0).setDate(JOB_EXEC_DATE).setImportSourceId(importId)
                                .createImportSourceData(); //FIXME zindex, date

                attrMap.put(getMar21FieldForString(tag), importSourceData);
            }
        }
        return attrMap;
    }

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
     * Returns specific column.
     * TODO 1.ensure only one column 2. F105
     * @param rowList spreadsheet rows
     * @return column order of F104, F105 field. Or -1 if no such column exists
     */
    public short findBibIdColumn(final List<ImportEntity.Row> rowList) {
        short order = 0;
        for (final ImportEntity.Row row: rowList) {
            final List<ImportEntity.Column> columns = row.getColumns();
            for (final ImportEntity.Column<String> col: columns) {
                if (col.getField().toString().equals(FunctionConstants.F104.getName())) {
                    return order;
                }
            }
            order++;
        }
        return -1;
    }

    /**
     * Returns specific column.
     * @param rowList spreadsheet rows
     * @return column order of F104, F105 field. Or -1 if no such column exists
     */
    public short findImageColumn(final List<ImportEntity.Row> rowList) {
        short order = 0;
        for (final ImportEntity.Row row: rowList) {
            final List<ImportEntity.Column> columns = row.getColumns();
            for (final ImportEntity.Column<String> col: columns) {
                if (isOAIFunction(col)) {
                    return order;
                }
            }
            order++;
        }
        return -1;
    }


    /**
     * Returns a list of bibIds from a specific column
     * TODO what if a row doesn't have a bibId or barcode?
     * @param rowList
     * @param columnNum
     * @return
     */
    public List<String> readBibIdsFromColumn(final List<ImportEntity.Row> rowList, final short columnNum) {
        if (columnNum == -1) {
            return Collections.EMPTY_LIST;
        }

        final List<String> bibIdList = new ArrayList<>();
        for (final ImportEntity.Row row: rowList) {
            final ImportEntity.Column<String> column = row.getColumns().get(columnNum);
            if (column.getField().getName().equals(FunctionConstants.F104.getName())) {
                bibIdList.add(column.getValue());
            }
        }
        return bibIdList;
    }

    /**
     * Determines if a column is an OAI field.
     * @param col A spreadsheet column value
     * @return whether a column matches an official OAI field
     */

    public boolean isOAIFunction(final ImportEntity.Column col) {
        final String fieldName = col.getField().getName();
        return (fieldName.equals(FunctionConstants.F104.getName()))
                || fieldName.equals(FunctionConstants.F105.getName()) ? true : false;
    }

    /**
     * Determines if a column is a function that should kick off some sort of image processing.
     * @param col A spreadsheet column value
     * @return whether a column matches an image processing function
     */

    public boolean isImageProcessingFunction(final ImportEntity.Column col) {
        final String fieldName = col.getField().getName();
        return (fieldName.equals(FunctionConstants.F3.getName()))? true : false;
    }

    /**
     * Writes import job
     *
     * @param ctx Context containing information about the job
     * @return newly minted job id
     */
    public Integer writeImportJob(ImportJobContext ctx) {
        ImportJobDAO importJobDAO = new ImportJobHibernateDAO();
        return importJobDAO.save(new ImportJobBuilder().setDate(JOB_EXEC_DATE).setJobDirectory(ctx.getJobDir()).
                setJobFile(ctx.getJobFile()).setUserId(ctx.getUserId()).createImportJob());
    }

    /**
     * Returns the first row. The first row represents the Excel headers.
     * @param rowList a list of ImportEntity.Row
     * @return an ImportEntity.Row
     */
    private ImportEntity.Row getHeaderRow(final List<ImportEntity.Row> rowList) {
        return rowList.get(0);
    }

    /**
     * Returns all the rows but the first one from the spreadsheet. The rows represents contents.
     * @param rowList a list of ImportEntity.Row
     * @return a list of ImportEntity.Row
     */
    private List<ImportEntity.Row> getContentRows(final List<ImportEntity.Row> rowList) {
        return rowList.subList(1, rowList.size());
    }

    /**
     * Sets OAI Provider. Subject to removal.
     * @param oaiProvider The default OaiProvider
     */
    public void setOaiProvider(final OaiProvider oaiProvider) {
        this.oaiProvider = oaiProvider;
    }

}
