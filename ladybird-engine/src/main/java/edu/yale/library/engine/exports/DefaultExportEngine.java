package edu.yale.library.engine.exports;

import edu.yale.library.engine.imports.ImportEntity;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultExportEngine extends AbstractExportEngine
{
    private final Logger logger = getLogger(this.getClass());

    private final static Integer DEFAULT_SHEET = 0; //todo

    @Override
    public List<ImportEntity.Row> doRead()
    {
        logger.debug("Reading rows from import table(s)");

        ExportReader reader = new ExportReader();
        List<ImportEntity.Row> list = reader.populateRows();

        logger.debug("list size=" + list.size());

        return list;
    }

    @Override
    public void doWrite(final List<ImportEntity.Row> list, final String pathName) throws IOException
    {
        logger.debug("Initiating write to spreadsheet. . .");

        ExportWriter exportWriter = new ExportWriter();
        exportWriter.write(list, pathName);
    }

}
