package edu.yale.library.engine.exports;

import edu.yale.library.beans.ImportJobContents;
import edu.yale.library.dao.ImportJobContentsDAO;
import edu.yale.library.dao.ImportJobExheadDAO;
import edu.yale.library.dao.hibernate.ImportJobContentsHibernateDAO;
import edu.yale.library.dao.hibernate.ImportJobExheadHibernateDAO;
import edu.yale.library.engine.cron.ExportEngineQueue;
import edu.yale.library.engine.imports.ImportEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads from import job tables
 */
public class ExportReader
{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int COL_COUNT = 30; //(No. of columns to read. will be replaced when fdids are in place)

    /**
     * TODO read from export queue
     * Reads from import tables.
     * @return
     */
    public List<ImportEntity.Row> populateRows()
    {
        List<ImportEntity.Row> resultList = new ArrayList();
        ImportJobContentsDAO dao = new ImportJobContentsHibernateDAO();
        ExportRequestEvent exportRequestEvent = ExportEngineQueue.getJob();
        final int importId = exportRequestEvent.getImportId();

        logger.debug("Num. entries={} for job={}", dao.getNumEntriesPerImportJob(importId), importId);

        final int expectedWriteRowCount = dao.getNumRowsPerImportJob(importId);

        logger.debug("Total number of expected spreadsheet rows to write={}", expectedWriteRowCount);

        //This will be used when fdids are in place:

        //ImportJobExheadDAO exheadDAO = new ImportJobExheadHibernateDAO();
        //final int COL_COUNT = exheadDAO.getNumEntriesPerImportJob(importId);
        //logger.debug("Exhead count={}", COL_COUNT);

        for (int i = 0; i < expectedWriteRowCount; i++)
        {
            List<ImportJobContents> importJobContentsList = dao.findByRow(i);

            //logger.debug("import job contents list size={}", importJobContentsList.size());

            final ImportEntity.Row row = new ImportEntity().new Row();
            for (int j = 0; j < COL_COUNT; j++)
            {
                ImportJobContents jobContents = importJobContentsList.get(j);
                row.getColumns().add(new ImportEntity().new Column(null, jobContents.getValue()));
            }
            resultList.add(row);
        }
        return resultList;
    }
}
