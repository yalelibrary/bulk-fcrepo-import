package edu.yale.library.engine.imports;

import edu.yale.library.engine.model.UnknownFunctionException;
import edu.yale.library.engine.model.ReadMode;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class DefaultImportEngine extends AbstractImportEngine
{
    private final Logger logger = getLogger(this.getClass());

    private final static Integer DEFAULT_SHEET = 0; //todo

    @Override
    public List<ImportEntity.Row> doRead(SpreadsheetFile file, ReadMode readMode) throws UnknownFunctionException
    {
        logger.debug("Reading spreadsheet: " + file.getAltName());

        ImportReader reader = new ImportReader(file, DEFAULT_SHEET, readMode);
        return reader.processSheet();
    }

    @Override
    public void doWrite(List<ImportEntity.Row> list)
    {
        logger.debug("Initiating write");

        ImportWriter importWriter = new ImportWriter();
        //TODO obtain file,dir,user
        importWriter.write(list, new ImportJobContextBuilder().userId(USER_ID).file("").dir("").build());
    }


}
