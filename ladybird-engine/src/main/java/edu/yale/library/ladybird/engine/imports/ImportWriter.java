package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.cron.queue.ImportImageConversionQueue;
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
import org.apache.commons.lang.time.DurationFormatUtils;
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

    private ImageFunctionProcessor imageFunctionProcessor;

    private ImportSourceProcessor importSourceProcessor;

    private final ImportJobContentsDAO dao = new ImportJobContentsHibernateDAO();

    private final ImportJobDAO importJobDAO = new ImportJobHibernateDAO();

    private final Date JOB_EXEC_DATE = new Date(); //TODO

    /**
     * Full cycle import writing
     *
     * @param importValue Abstraction on top of List<ImportEntity.Row>
     * @param importJobRequest  Job Context
     * @return Import Id
     */
    public synchronized int write(ImportValue importValue, final ImportJobRequest importJobRequest) throws Exception {
        try {
            final int importId = writeImportJob(importJobRequest);

            logger.debug("Saved import job. importId={}", importId);

            //save which functions exist
            Set<FieldConstant> fieldConstants = importValue.getAllFunctions();

            //note 2 columns are added:
            logger.debug("Processing oids for importId={}", importId);
            importValue = writeF1(importValue, importJobRequest.getProjectId());

            logger.debug("Process fdid111 for importId={}", importId);
            importValue = processFdid111(importValue);

            writeExHead(importId, importValue.getHeaderRow().getColumns());
            logger.debug("Wrote import job exheads for importId={}", importId);
            writeContents(importId, importValue, fieldConstants);
            logger.debug("Wrote import job contents for importId={}", importId);
            return importId;
        } catch (Exception e) {
            logger.error("Error in import writer for request id={}", importJobRequest.getRequestId());
            throw e;
        }
    }

    private synchronized ImportValue writeF1(final ImportValue importValue, final int projectId) {
        // Process F1
        if (!importValue.fieldConstantsInExhead(FunctionConstants.F1)) {
            return processF1(importValue, projectId);
        }
        return importValue;
    }

    /**
     * Write header ("ex head")
     *
     * @param importId import id of the job
     * @param list     list of contents
     */
    public synchronized void writeExHead(final int importId, final List<Import.Column> list) {
        logger.trace("Writing spreadsheet headers. Columns list size={}", list.size());

        final ImportJobExheadDAO dao = new ImportJobExheadHibernateDAO();
        int col = 0;

        final List<ImportJobExhead> exheads = new ArrayList<>();

        for (final Import.Column column : list) {
            ImportJobExhead entry = new ImportJobExheadBuilder().setImportId(importId).setCol(col).
                    setDate(JOB_EXEC_DATE).setValue(column.field.getName()).createImportJobExhead();
            exheads.add(entry);
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
    public synchronized void writeContents(final int importId, ImportValue importEntity,
                                           Set<FieldConstant> sheetFieldConstants) throws Exception {
        try {
            logger.info("Sheet contains FieldConstants={}", sheetFieldConstants.toString());

            checkImportIdPreConditions(importId);

            // Write import source data
            importSourceProcessor.process(importId, oaiProvider, importEntity);

            if (sheetFieldConstants.contains(FunctionConstants.F1)) {
                if (sheetFieldConstants.contains(FunctionConstants.F3)) { //F1 is present -> update oid with attached F3
                    logger.info("Spreadsheet has col F1, and F3");
                    imageFunctionProcessor.createObjectFiles(importId, importEntity);
                    addImageConversionJob(importId, importEntity);
                } else { //F1 is present, but no F3. Nothing to do.
                    logger.info("No F3 column found in spreadsheet. Nothing to do.");
                }
            } else {
                if (sheetFieldConstants.contains(FunctionConstants.F3)) { //no F1 -> generate oid and attach F3
                    logger.info("Spreadsheet doesn't have F1, but F3");
                    imageFunctionProcessor.createObjectFiles(importId, importEntity);
                    addImageConversionJob(importId, importEntity);
                } else { //Neither F1, nor F3 present -> generate blank
                    logger.info("Spreadsheet doesn't have F1, nor F3");
                    importEntity = addF3Column(importEntity);
                    imageFunctionProcessor.createObjectFiles(importId, importEntity);
                    addImageConversionJob(importId, importEntity);
                }
            }

            //Process F4,F6 (check step requirement)
            if (processF4F6(importEntity)) {
                processComplex(importEntity);
            }

            if (processF5F6(importEntity)) {
                processComplexF5(importEntity);
            }

            if (processF7F8(importEntity)) {
                processComplexF7(importEntity);
            }

            final List<Import.Row> rowList = importEntity.getContentRows();

            final ImportJobContentsBuilder imjBuilder = new ImportJobContentsBuilder();

            final List<ImportJobContents> importJobContentsList = new ArrayList<>();

            // Save to DB import job contents (N.B. f104/f105 column(s) also persisted):
            for (int i = 0; i < rowList.size(); i++) {
                final Import.Row row = rowList.get(i);
                final List<Import.Column> cols = row.getColumns();

                for (int j = 0; j < cols.size(); j++) {
                    final Import.Column<String> col = cols.get(j);
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

    private void addImageConversionJob(int importId, ImportValue importValue) {
        ImageConversionRequestEvent event = new ImageConversionRequestEvent();
        event.setImportValue(importValue);
        event.setImportId(importId);
        event.setExportDirPath(imageFunctionProcessor.getProjectDir());

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

    private ImportValue addF3Column(final ImportValue importValue) {
        //checkExheadAndContentColsMatch(importEntityValue); //sanity check
        //or whatever dummy tif. It won't be found anyways. Should proably pass a stream.
        // Depends on major refactoring of media function processor.
        return importValue.write(importValue, FunctionConstants.F3, "test8787.tif");
    }

    /**
     * Writes to import job table
     *
     * @param importJobRequest Context containing information about the job
     * @return minted job id
     */
    public Integer writeImportJob(final ImportJobRequest importJobRequest) {
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
    public void setImageFunctionProcessor(final ImageFunctionProcessor imageFunctionProcessor) {
        this.imageFunctionProcessor = imageFunctionProcessor;
    }

    public void setImportSourceProcessor(ImportSourceProcessor importSourceProcessor) {
        this.importSourceProcessor = importSourceProcessor;
    }

    private ImportValue processF1(ImportValue importValue, int projectId) {
        OidMinter oidMinter = new OidMinter();
        long elapsed = System.currentTimeMillis();
        ImportValue modifiedImportEntity =  oidMinter.write(importValue, projectId);
        logger.debug("Completed processing F1 in={} rows={}",
                DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - elapsed),
                modifiedImportEntity.getContentRows().size());
        return modifiedImportEntity;
    }

    private ImportValue processFdid111(ImportValue importValue) {
        HandleMinter handleMinter = new HandleMinter();
        return handleMinter.write(importValue);
    }

    private void processComplex(ImportValue importValue) {
        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF4(importValue);
    }

    private void processComplexF5(ImportValue importValue) {
        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF5(importValue);
    }

    private void processComplexF7(ImportValue importValue) {
        ComplexProcessor complexProcessor = new ComplexProcessor();
        complexProcessor.processF7(importValue);
    }

    private boolean processF4F6(final ImportValue importValue) {
        return importValue.hasFunction(FunctionConstants.F1)
                && importValue.hasFunction(FunctionConstants.F4)
                && importValue.hasFunction(FunctionConstants.F6);
    }

    private boolean processF5F6(final ImportValue importValue) {
        return importValue.hasFunction(FunctionConstants.F1) //note
                && importValue.hasFunction(FunctionConstants.F5)
                && importValue.hasFunction(FunctionConstants.F6);
    }

    private boolean processF7F8(final ImportValue importValue) {
        return importValue.hasFunction(FunctionConstants.F1) //note
                && importValue.hasFunction(FunctionConstants.F7)
                && importValue.hasFunction(FunctionConstants.F8);
    }

}
