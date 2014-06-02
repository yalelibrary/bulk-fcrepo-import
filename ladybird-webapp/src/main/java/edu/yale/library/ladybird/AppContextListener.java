package edu.yale.library.ladybird;

import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.kernel.KernelBootstrap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

/**
 * Not meant to be used directly. Subject to modification.
 */
public class AppContextListener implements ServletContextListener {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppContextListener.class);

    private final KernelBootstrap kernelBootstrap = new KernelBootstrap();

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        kernelBootstrap.stop();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        kernelBootstrap.init();
        ExportBus exportBus = new ExportBus(); //TODO
        exportBus.init();

        // Load initial fdid
        // TODO revisit concept/logic for init fdid for spreadsheet and db
        try {
            FieldDefinitionInitializer fieldDefinitionInitializer = new FieldDefinitionInitializer();
            fieldDefinitionInitializer.setTextFieldDefsMap();
            fieldDefinitionInitializer.setInitialFieldDefinitionDb();
        } catch (IOException e) {
            logger.error("Error in fdid init", e); //ignore
        }

        //Load properties file
        try {
            SettingsInitializer settingsInitializer = new SettingsInitializer();
            settingsInitializer.loadAndStore();
        } catch (Exception e) {
            logger.error("Error in setting seettings", e); //ignore
        }
    }

}
