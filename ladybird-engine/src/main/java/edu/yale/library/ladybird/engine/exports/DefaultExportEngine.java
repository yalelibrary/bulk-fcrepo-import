package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.cron.JobTracker;
import edu.yale.library.ladybird.engine.imports.ImportEntity;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultExportEngine extends AbstractExportEngine {
    private final Logger logger = getLogger(this.getClass());

    @Override
    public List<ImportEntity.Row> doRead() {
        logger.debug("Reading rows from import table(s)");

        final ExportReader reader = new ExportReader();
        final List<ImportEntity.Row> list = reader.readRowsFromImportTables();

        logger.debug("Read list size={}", list.size());

        return list;
    }

    @Override
    public void doWrite(final List<ImportEntity.Row> list, final String pathName) throws IOException {
        logger.debug("Initiating write to spreadsheet to path={}", pathName);

        final ExportWriter exportWriter = new ExportWriter();
        exportWriter.write(list, pathName);
        JobTracker.steps++; //TODO
    }

}
