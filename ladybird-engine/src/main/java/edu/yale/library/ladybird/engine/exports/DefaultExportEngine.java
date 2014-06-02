package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.ImportEntity;
import edu.yale.library.ladybird.engine.imports.ImportJobCtx;
import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.slf4j.Logger;

import java.io.File;
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

    /**
     * Writes spreadsheet to disk.
     * @param list content
     * @param relativePath relative path, e.g. project1/a.xlsx
     * @throws IOException
     */

    @Override
    public void doWrite(final List<ImportEntity.Row> list, final String relativePath) throws IOException {
        logger.debug("Initiating write to spreadsheet to relative path={}", relativePath);
        final ExportWriter exportWriter = new ExportWriter();
        final String fullPath = getWritePath(relativePath);
        logger.debug("Initiating write to spreadsheet to path={}", fullPath);
        exportWriter.write(list, fullPath);
    }

    /** Returns absolute write appeneded with project */
    private String getWritePath(final String relativePath) {

        //FIXME for IT value is retured as is.
        // TODO the test ladybird.properties should either contain this path or the reader should be able to recover from the error
        if (ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH == null) {
            logger.error("Returning relative path as is");
            return relativePath;
        }

        SettingsDAO settingsDAO = new SettingsHibernateDAO();

        final Settings settings = settingsDAO.findByProperty(ApplicationProperties.IMPORT_ROOT_PATH_ID);

        if (settings == null) {
            logger.debug("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH + File.separator + relativePath;
        } else {
            return settings.getValue() + File.separator + relativePath;
        }
    }

}
