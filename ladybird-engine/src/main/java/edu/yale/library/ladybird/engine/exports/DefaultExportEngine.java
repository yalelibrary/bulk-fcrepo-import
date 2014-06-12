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

    //TODO inject DAO(s):
    final ExportReader reader = new ExportReader();
    final ExportWriter exportWriter = new ExportWriter();
    final SettingsDAO settingsDAO = new SettingsHibernateDAO();

    /**
     *
     * @return
     */
    @Override
    public ImportJobCtx doRead() {
        final ImportJobCtx importJobCtx = reader.read();
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
        final String fullPath = getWritePath(relativePath);
        logger.debug("Initiating write to spreadsheet to relative path={}, path={}", relativePath, fullPath);
        exportWriter.write(list, fullPath);
    }

    /**
     * Returns absolute write appended with project
     * @param relativePath
     * @return
     */
    private String getWritePath(final String relativePath) {
        logger.trace("Looking up relative path={}", relativePath);
        if (ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH == null) {
            logger.error("No import root path. Returning relative path as is.");
            return relativePath;
        }

        final Settings settings = settingsDAO.findByProperty(ApplicationProperties.IMPORT_ROOT_PATH_ID);

        if (settings == null) {
            logger.debug("No db configured property={}", ApplicationProperties.IMPORT_ROOT_PATH_ID);
            return ApplicationProperties.CONFIG_STATE.IMPORT_ROOT_PATH + File.separator + relativePath;
        } else {
            return settings.getValue() + File.separator + relativePath;
        }
    }

}
