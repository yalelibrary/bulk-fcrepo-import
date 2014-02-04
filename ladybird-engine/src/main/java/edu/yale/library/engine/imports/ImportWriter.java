package edu.yale.library.engine.imports;


import edu.yale.library.beans.*;
import edu.yale.library.dao.ImportFileDAO;
import edu.yale.library.dao.ImportJobContentsDAO;
import edu.yale.library.dao.ImportJobDAO;
import edu.yale.library.dao.ImportJobExheadDAO;
import edu.yale.library.dao.hibernate.ImportFileHibernateDAO;
import edu.yale.library.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.dao.hibernate.ImportJobHibernateDAO;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * Simple implementation of import job writing
 */
public class ImportWriter
{
    private final Logger logger = getLogger(this.getClass());

    Date JOB_EXEC_DATE = new Date(System.currentTimeMillis()); //todo

    /**
     * Full cycle import writing (sans file import).
     * @param list
     * @param ctx import job context containing information about the job
     */
    public void write(final List<ImportEntity.Row> list, ImportJobContext ctx)
    {
        final int importId = writeImportJob(ctx);
        //header
        writeExHead(importId, getHeaderRow(unmodifiableList(list)).getColumns());
        //contents
        writeContents(importId, getContentRows(unmodifiableList(list)));
    }

    /**
     * Write header
     * @param importId
     * @param list
     */
    public void writeExHead(final int importId, final List<ImportEntity.Column> list)
    {
        ImportJobExheadDAO dao = new ImportJobExheadHibernateDAO();
        int col = 0;
        logger.debug("Excel spreadsheet columns size:" + list.size());
        for (ImportEntity.Column column: list)
        {
            ImportJobExhead entry = new ImportJobExheadBuilder().setImportId(importId).setCol(col).
                    setDate(JOB_EXEC_DATE).setValue(column.field.getName()).createImportJobExhead();
            dao.save(entry);
            col++;
        }
    }

    /**
     * Writes content body
     * @param importId
     * @param list
     */
    public void writeContents(final int importId, final List<ImportEntity.Row> list)
    {
        logger.debug("Writing spreadsheet body contents");
        logger.debug("List size={}", list.size());

        List<ImportJobContents> jobContentsList = new ArrayList<>(); //TODO (for bulk save)

        ImportJobContentsDAO dao = new ImportJobContentsHibernateDAO();

        for (int r = 0; r < list.size(); r++) //todo chk init values
        {
            final ImportEntity.Row row = list.get(r);
            List<ImportEntity.Column> columns = row.getColumns();

            for (int c = 0; c <  columns.size(); c++)
            {
                ImportEntity.Column<String> col = columns.get(c);

                logger.debug("Row={}, Col={}, Columns={}", r, c, columns.size());
                ImportJobContents entry = new ImportJobContentsBuilder().setImportId(importId).setDate(JOB_EXEC_DATE).
                        setCol(c).setRow(r).setValue(col.getValue()).build();
                dao.save(entry); //TODO or save list
            }
            //jobContentsList.add(entry);
        }
    }

    /**
     * Writes import job
     * @param ctx
     * @return newly minted job id
     */
    public Integer writeImportJob(ImportJobContext ctx)
    {
        ImportJobDAO importJobDAO = new ImportJobHibernateDAO();
        return importJobDAO.save(new ImportJobBuilder().setDate(JOB_EXEC_DATE).setJobDirectory(ctx.getJobDir()).
                setJobFile(ctx.getJobFile()).setUserId(ctx.getUserId()).createImportJob());
    }

    private ImportEntity.Row getHeaderRow(final List<ImportEntity.Row> list)
    {
        return list.get(0);
    }

    private List<ImportEntity.Row> getContentRows(final List<ImportEntity.Row> list)
    {
        return list.subList(1, list.size());
    }

}
