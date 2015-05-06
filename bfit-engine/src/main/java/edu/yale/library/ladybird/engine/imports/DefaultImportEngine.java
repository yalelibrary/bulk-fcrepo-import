package edu.yale.library.ladybird.engine.imports;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class DefaultImportEngine extends AbstractImportEngine {

    private final Logger logger = getLogger(this.getClass());

    private static final Integer DEFAULT_SHEET = 0; //TODO

    @Override
    public List<Import.Row> doRead(final Spreadsheet file, final ReadMode readMode)
            throws ImportReaderValidationException, IOException {
        logger.debug("Initiating read={}", file.getFileName());

        ImportReader reader = new ImportReader(file, DEFAULT_SHEET, readMode);
        return reader.read();
    }

    @Override
    public int doWrite(final List<Import.Row> list) {
        logger.debug("Initiating write (userId={} projectId={} size={})", USER_ID, PROJECT_ID, list.size());

        ImportWriter importWriter = new ImportWriter();
        importWriter.setOaiProvider(oaiProvider);  //TODO
        importWriter.setImageFunctionProcessor(imageFunctionProcessor); //TODO
        importWriter.setImportSourceProcessor(importSourceProcessor); //TODO

        ImportValue importValue = new ImportValue(list);

        try {
            return importWriter.write(importValue,
                    new ImportJobRequestBuilder().userId(USER_ID).file("").dir("").projectId(PROJECT_ID)
                            .build());
        } catch (Exception e) {
            throw new ImportEngineException(e);
        }
    }

    //TODO consolidate or use different param construct (instead of list of row)
    @Override
    public int doWrite(final List<Import.Row> list, Spreadsheet spreadsheet, int requestId) {
        logger.debug("Initiating write, userId={} projectId={} list size={}", USER_ID, PROJECT_ID, list.size());

        ImportWriter importWriter = new ImportWriter();
        importWriter.setOaiProvider(oaiProvider);  //TODO
        importWriter.setImageFunctionProcessor(imageFunctionProcessor); //TODO
        importWriter.setImportSourceProcessor(importSourceProcessor); //TODO

        ImportValue importValue = new ImportValue(list);
        try {
            return importWriter.write(importValue,
                    new ImportJobRequestBuilder().userId(USER_ID).file(spreadsheet.getFileName())
                            .dir("").requestId(requestId).projectId(PROJECT_ID).build());
        } catch (ContextedRuntimeException cre) {
            ImportEngineException importEngineException = new ImportEngineException(cre);
            importEngineException.setContextValue("Row", cre.getFirstContextValue("Row"));
            importEngineException.setContextValue("Column", cre.getFirstContextValue("Column"));
            throw importEngineException;
        } catch (Exception e) {
            throw new ImportEngineException(e);
        }
    }

    public DefaultImportEngine(int userId, int projectId) {
        super.USER_ID = userId;
        super.PROJECT_ID = projectId;
    }
}
