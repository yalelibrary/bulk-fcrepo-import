package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportJobCtx;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultExportEngine extends AbstractExportEngine {
    private final Logger logger = getLogger(this.getClass());

     @Override
    public ImportJobCtx doRead() {

        final ExportReader reader = new ExportReader();
        final ImportJobCtx importJobCtx = reader.readRowsFromImportTables();

        logger.debug("Read list size={}", importJobCtx.getImportJobList().size());

        return importJobCtx;
    }

    @Override
    public void doWrite(final List<ImportEntity.Row> list, final String pathName) throws IOException {
        logger.debug("Initiating write to spreadsheet to path={}", pathName);

        final ExportWriter exportWriter = new ExportWriter();
        exportWriter.write(list, pathName);
    }

}
