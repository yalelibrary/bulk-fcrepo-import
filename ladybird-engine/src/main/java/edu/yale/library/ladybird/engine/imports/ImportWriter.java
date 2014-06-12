package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.FdidMarcMappingUtil;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.model.LocalIdMarcImportSource;
import edu.yale.library.ladybird.engine.model.LocalIdentifier;
import edu.yale.library.ladybird.engine.oai.Marc21Field;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.entity.ImportJobBuilder;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobContentsBuilder;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.entity.ImportJobExheadBuilder;
import edu.yale.library.ladybird.persistence.dao.FieldMarcMappingDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import org.slf4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


public class ImportWriter {
    private final Logger logger = getLogger(this.getClass());

    //TODO inject dao(s), etc

    private OaiProvider oaiProvider;
    private MediaFunctionProcessor mediaFunctionProcessor;
    private final FieldMarcMappingDAO fieldMarcMappingDAO = new FieldMarcMappingHibernateDAO();
    private final ImportJobContentsDAO dao = new ImportJobContentsHibernateDAO();
    private final ImportJobDAO importJobDAO = new ImportJobHibernateDAO();
    private final Date JOB_EXEC_DATE = new Date(System.currentTimeMillis()); //TODO pass

    /**
     * Full cycle import writing
     *
     * @param importEntityValue Abstraction on top of List<ImportEntity.Row>
     * @param ctx Job Context
     * @return Import Id
     */
    public int write(final ImportEntityValue importEntityValue, final ImportJobContext ctx) throws Exception {
        try {
            final int importId = writeImportJob(ctx);
            writeExHead(importId, importEntityValue.getHeaderRow().getColumns());
            writeContents(importId, importEntityValue); //change to only columns
            return importId;
        } catch (Exception e) {
            logger.error("Error writing.", e);
            throw e;
        }
    }

    /**
     * Write header ("ex head")
     *
     * @param importId import id of the job
     * @param list list of contents
     */
    public void writeExHead(final int importId, final List<ImportEntity.Column> list) {
        logger.debug("Writing spreadsheet headers. Columns list size={}", list.size());

        final ImportJobExheadDAO dao = new ImportJobExheadHibernateDAO();
        int col = 0;

        for (final ImportEntity.Column column : list) {
            ImportJobExhead entry = new ImportJobExheadBuilder().setImportId(importId).setCol(col).
                    setDate(JOB_EXEC_DATE).setValue(column.field.getName()).createImportJobExhead();
            dao.save(entry);
            logger.trace("Saved={}", entry.toString());
            col++; //TODO bug
        }
    }

    /**
     * Writes body to db
     *
     * @param importId import id of the job
     * @param importEntityValue helper datastructre representing list<rows></rows>
     */
    @SuppressWarnings("unchecked")
    public void writeContents(final int importId, final ImportEntityValue importEntityValue) {

        //Write import source data
        final List<LocalIdentifier<String>> bibIdList = LocalIdentifier.getBibIds(importEntityValue);
        final ImportSourceDataReader importSourceDataReader = new ImportSourceDataReader();
        final List<FieldMarcMapping> fieldMarcMappingList = fieldMarcMappingDAO.findAll();
        final Map<Marc21Field, FieldMarcMapping> marc21FieldMap = new FdidMarcMappingUtil().buildMarcFdidMap(fieldMarcMappingList);
        final List<LocalIdMarcImportSource> importSourceLocalIdList =
                importSourceDataReader.readBibIdMarcData(oaiProvider, bibIdList, importId);
        ImportSourceDataWriter importSourceDataWriter = new ImportSourceDataWriter();
        importSourceDataWriter.persistMarcData(importSourceLocalIdList, importId);

        final List<ImportEntity.Row> rowList = importEntityValue.getContentRows();
        logger.debug("Writing spreadsheet body contents. Row list size={}", rowList.size());


        //Process F1
        if (importEntityValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            final int columnWithF1Field = importEntityValue.getFunctionPosition(FunctionConstants.F1);
        }

        //Process F3 without F1 (to keep tests working)
        if (importEntityValue.fieldConstantsInExhead(FunctionConstants.F3)
                && !importEntityValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            final int f3ColumnNum = importEntityValue.getFunctionPosition(FunctionConstants.F3);
            mediaFunctionProcessor.process(rowList, f3ColumnNum);
            logger.debug("Done media processing.");
        }

        //Process F3 with F1
        if (importEntityValue.fieldConstantsInExhead(FunctionConstants.F3)
                && importEntityValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            final int f3ColumnNum = importEntityValue.getFunctionPosition(FunctionConstants.F3);
            final int f1columnNum = importEntityValue.getFunctionPosition(FunctionConstants.F1);
            mediaFunctionProcessor.process(importId, rowList, f3ColumnNum, f1columnNum);
            logger.debug("Done media processing.");
        }

        //Save all to DB table import job contents (N.B. f104/f105 column(s) also persisted):
        for (int i = 0; i < rowList.size(); i++) {
            final ImportEntity.Row row = rowList.get(i);
            final List<ImportEntity.Column> columnsList = row.getColumns();

            for (int j = 0; j < columnsList.size(); j++) {
                final ImportEntity.Column<String> col = columnsList.get(j);
                final ImportJobContents imjContentsEntry = new ImportJobContentsBuilder()
                        .setImportId(importId).setDate(JOB_EXEC_DATE).
                                setCol(j).setRow(i).setValue(col.getValue()).build();
                dao.save(imjContentsEntry);
            }
        }
    }

    /**
     * Writes to import job table
     *
     * @param ctx Context containing information about the job
     * @return minted job id
     */
    public Integer writeImportJob(final ImportJobContext ctx) {
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
