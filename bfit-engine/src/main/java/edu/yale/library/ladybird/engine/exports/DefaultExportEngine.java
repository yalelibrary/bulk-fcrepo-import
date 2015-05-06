package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.Import;
import edu.yale.library.ladybird.engine.imports.ImportContext;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class DefaultExportEngine extends AbstractExportEngine {

    private final Logger logger = getLogger(this.getClass());

    private final ImportContextReader reader = new ImportContextReader();

    private final ExportWriter exportWriter = new ExportWriter();

    @Override
    public ImportContext doRead() {
        final ImportContext importContext = reader.read();
        return importContext;
    }

    /**
     * Writes spreadsheet to disk.
     * @param list content
     * @param path full path to disk file
     * @throws IOException
     */
    @Override
    public void doWrite(final List<Import.Row> list, final String path) throws IOException {
        logger.debug("Writing file to={}", path);
        exportWriter.write(list, path);
    }

    /**
     * Writes spreadsheet to disk.
     * @param list content
     * @param path full path to disk file
     * @throws IOException
     */
    @Override
    public void doWriteSheets(final List<ExportSheet> list, final String path) throws IOException {
        logger.debug("Writing file to={}", path);
        exportWriter.writeSheets(list, path);
    }

}
