package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.cron.ImportImageConversionQueue;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.engine.oai.ImportSourceProcessor;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.entity.FieldConstant;
import edu.yale.library.ladybird.entity.ImportJobBuilder;
import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.entity.ImportJobContentsBuilder;
import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.entity.ImportJobExheadBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.slf4j.LoggerFactory.getLogger;


public class ImportWriter {
    private final Logger logger = getLogger(this.getClass());

    //TODO inject dao(s), etc

    private OaiProvider oaiProvider;
    private MediaFunctionProcessor mediaFunctionProcessor;
    private ImportSourceProcessor importSourceProcessor;
    private final ImportJobContentsDAO dao = new ImportJobContentsHibernateDAO();
    private final ImportJobDAO importJobDAO = new ImportJobHibernateDAO();
    private final Date JOB_EXEC_DATE = new Date(); //TODO

    /**
     * Full cycle import writing
     *
     * @param importEntityValue Abstraction on top of List<ImportEntity.Row>
     * @param importJobRequest  Job Context
     * @return Import Id
     */
    public synchronized int write(ImportEntityValue importEntityValue, final ImportJobRequest importJobRequest) throws Exception {
        try {
            final int importId = writeImportJob(importJobRequest);

            //save which functions exist
            Set<FieldConstant> fieldConstants = importEntityValue.getAllFunctions();

            //note 2 columns are added:
            importEntityValue = writeF1(importEntityValue, importJobRequest.getProjectId());
            importEntityValue = processFdid111(importEntityValue);

            writeExHead(importId, importEntityValue.getHeaderRow().getColumns());
            writeContents(importId, importEntityValue, fieldConstants);
            return importId;
        } catch (Exception e) {
            logger.error("Error in import writer for request id={}", importJobRequest.getRequestId());
            throw e;
        }
    }

    private synchronized ImportEntityValue writeF1(final ImportEntityValue importEntityValue, final int projectId) {
        // Process F1
        if (!importEntityValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            return processF1(importEntityValue, projectId);
        }
        return importEntityValue;
    }

    /**
     * Write header ("ex head")
     *
     * @param importId import id of the job
     * @param list     list of contents
     */
    public synchronized void writeExHead(final int importId, final List<ImportEntity.Column> list) {
        logger.trace("Writing spreadsheet headers. Columns list size={}", list.size());

        final ImportJobExheadDAO dao = new ImportJobExheadHibernateDAO();
        int col = 0;

        final List<ImportJobExhead> exheads = new ArrayList<>();

        for (final ImportEntity.Column column : list) {
            ImportJobExhead entry = new ImportJobExheadBuilder().setImportId(importId).setCol(col).
                    setDate(JOB_EXEC_DATE).setValue(column.field.getName()).createImportJobExhead();
            exheads.add(entry);
            logger.trace("Saved={}", entry.toString());
            col++;
        }

        dao.saveList(exheads);
    }

    /**
     * Writes body rows to import_job_contents
     *
     * @param importId     import id of the job
     * @param importEntity helper data structure representing list<rows>
     */
    @SuppressWarnings("unchecked")
    public synchronized void writeContents(final int importId, ImportEntityValue importEntity,
                                           Set<FieldConstant> sheetFieldConstants) throws Exception {
        try {
            logger.debug("Writing import job contents for importId={}", importId);
            logger.info("Sheet contains FieldConstants={}", sheetFieldConstants.toString());

            checkImportIdPreConditions(importId);

            // Write import source data
            importSourceProcessor.process(importId, oaiProvider, importEntity);

            if (sheetFieldConstants.contains(FunctionConstants.F1)) {
                if (sheetFieldConstants.contains(FunctionConstants.F3)) { //F1 is present -> update oid with attached F3
                    logger.info("Spreadsheet has col F1, and F3");
                    mediaFunctionProcessor.createObjectFiles(importId, importEntity);
                    addImageConversionJob(importId, importEntity);
                } else { //F1 is present, but no F3. Nothing to do.
                    logger.info("No F3 column found in spreadsheet. Nothing to do.");
                }
            } else {
                if (sheetFieldConstants.contains(FunctionConstants.F3)) { //no F1 -> generate oid and attach F3
                    logger.info("Spreadsheet doesn't have F1, but F3");
                    mediaFunctionProcessor.createObjectFiles(importId, importEntity);
                    addImageConversionJob(importId, importEntity);
                } else { //Neither F1, nor F3 present -> generate blank
                    logger.info("Spreadsheet doesn't have F1, nor F3");
                    importEntity = addF3Column(importEntity);
                    mediaFunctionProcessor.createObjectFiles(importId, importEntity);
                    addImageConversionJob(importId, importEntity);
                }
            }

            //Process F4,F6 (check step requirement)
            if (processF4F6(importEntity)) {
                logger.debug("Processing F4, F6");
                processComplex(importEntity);
            }

            if (processF5F6(importEntity)) {
                processComplexF5(importEntity);
            }

            if (processF7F8(importEntity)) {
                processComplexF7(importEntity);
            }

            final List<ImportEntity.Row> rowList = importEntity.getContentRows();

            final ImportJobContentsBuilder imjBuilder = new ImportJobContentsBuilder();

            final List<ImportJobContents> importJobContentsList = new ArrayList<>();

            // Save to DB import job contents (N.B. f104/f105 column(s) also persisted):
            for (int i = 0; i < rowList.size(); i++) {
                final ImportEntity.Row row = rowList.get(i);
                final List<ImportEntity.Column> cols = row.getColumns();

                for (int j = 0; j < cols.size(); j++) {
                    final ImportEntity.Column<String> col = cols.get(j);
                    importJobContentsList.add(imjBuilder.setImportId(importId).setDate(JOB_EXEC_DATE)
                            .setCol(j).setRow(i).setValue(col.getValue()).build());
                }
            }

            dao.saveList(importJobContentsList);

            //logger.info("Wrote to import job contents num. rows={} for importId={}", dao.findByImportId(importId).size(), importId);
            checkImportIdPostConditions(importId);
        } catch (Exception e) {
            logger.error("Error writing import job contents.");
            throw e;
        }
    }

    private void addImageConversionJob(int importId, ImportEntityValue importEntityValue) {
        ImageConversionRequestEvent event = new ImageConversionRequestEvent();
        event.setImportEntityValue(importEntityValue);
        event.setImportId(importId);
        event.setExportDirPath(mediaFunctionProcessor.getProjectDir());

        logger.debug("Adding event to image conversion queue={}", event);

        ImportImageConversionQueue.addJob(event);
    }

    private void checkImportIdPreConditions(final int importId) {
        checkArgument(importId > -1, "Import Id must be positive");
        checkArgument(dao.findByImportId(importId).size() == 0);  //TODO dao count method
    }

    private void checkImportIdPostConditions(final int importId) {
        checkState(dao.findByImportId(importId).size() > 0, "Expected number of rows written must not be 0");
    }

    private ImportEntityValue addF3Column(final ImportEntityValue importEntityValue) {
        //checkExheadAndContentColsMatch(importEntityValue); //sanity check
        //or whatever dummy tif. It won't be found anyways. Should proably pass a stream.
        // Depends on major refactoring of media function processor.
        return importEntityValue.write(importEntityValue, FunctionConstants.F3, "test8787.tif");
    }

    /**
     * Writes to import job table
     *
     * @param importJobRequest Context containing information about the job
     * @return minted job id
     */
    public Integer writeImportJob(final ImportJobRequest importJobRequest) {
        logger.info("Saving import job={}", importJobRequest.toString());

        return importJobDAO.save(new ImportJobBuilder().setDate(JOB_EXEC_DATE).setJobDirectory(importJobRequest.getJobDir()).
                setJobFile(importJobRequest.getJobFile()).setUserId(importJobRequest.getUserId())
                .setRequestId(importJobRequest.getRequestId()).createImportJob());
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

    public void setImportSourceProcessor(ImportSourceProcessor importSourceProcessor) {
        this.importSourceProcessor = importSourceProcessor;
    }

    private ImportEntityValue processF1(ImportEntityValue importEntityValue, int projectId) {
        OidMinter oidMinter = new OidMinter();
        return oidMinter.write(importEntityValue, projectId);
    }

    private ImportEntityValue processFdid111(ImportEntityValue importEntityValue) {
        HandleMinter handleMinter = new HandleMinter();
        return handleMinter.write(importEntityValue);
    }

    private void processComplex(ImportEntityValue importEntityValue) {
        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF4(importEntityValue);
    }

    private void processComplexF5(ImportEntityValue importEntityValue) {
        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF5(importEntityValue);
    }

    private void processComplexF7(ImportEntityValue importEntityValue) {
        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF7(importEntityValue);
    }

    private boolean processF4F6(final ImportEntityValue importEntityValue) {
        return importEntityValue.hasFunction(FunctionConstants.F1)
                && importEntityValue.hasFunction(FunctionConstants.F4)
                && importEntityValue.hasFunction(FunctionConstants.F6);
    }

    private boolean processF5F6(final ImportEntityValue importEntityValue) {
        return importEntityValue.hasFunction(FunctionConstants.F1) //note
                && importEntityValue.hasFunction(FunctionConstants.F5)
                && importEntityValue.hasFunction(FunctionConstants.F6);
    }

    private boolean processF7F8(final ImportEntityValue importEntityValue) {
        return importEntityValue.hasFunction(FunctionConstants.F1) //note
                && importEntityValue.hasFunction(FunctionConstants.F7)
                && importEntityValue.hasFunction(FunctionConstants.F8);
    }

}
