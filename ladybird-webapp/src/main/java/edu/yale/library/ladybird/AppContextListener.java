package edu.yale.library.ladybird;

import edu.yale.library.ladybird.engine.RolesPermissionsLoader;
import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.oai.FdidMarcMappingUtil;
import edu.yale.library.ladybird.entity.event.RollbackEventType;
import edu.yale.library.ladybird.entity.event.UserEditEvent;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import edu.yale.library.ladybird.persistence.dao.EventTypeDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.EventTypeHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;

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
            fieldDefinitionInitializer.setInitialFieldDefinitionDb();
        } catch (IOException e) {
            logger.error("Error in fdid init", e); //ignore
        }

        // Load initial fdid marc mappings
        try {
            FdidMarcMappingUtil fdidMarcMappingInitializer = new FdidMarcMappingUtil();
            fdidMarcMappingInitializer.setFieldMarcMappingDAO(new FieldMarcMappingHibernateDAO()); //FIXME
            fdidMarcMappingInitializer.setInitialFieldMarcDb();
        } catch (Exception e) {
            logger.error("Error in fdid init", e); //ignore
        }

        //Load properties file
        try {
            SettingsInitializer settingsInitializer = new SettingsInitializer();
            settingsInitializer.loadAndStore();
        } catch (Exception e) {
            logger.error("Error in setting settings", e); //ignore
        }

        //Add Object Events
        //FIXME: need to check to ensure that if the DB already contains these events, they are not added twice
        try {
            EventTypeDAO eventTypeDAO = new EventTypeHibernateDAO();
            eventTypeDAO.save(new UserEditEvent());
            eventTypeDAO.save(new RollbackEventType());
            logger.debug("Event types are={}", eventTypeDAO.findAll().toString());
        } catch (Exception e) {
            logger.error("Error setting event types", e);
        }

        //Add role permissions
        try {
            RolesPermissionsLoader rolesPermissionsLoader = new RolesPermissionsLoader();
            rolesPermissionsLoader.load(); //TODO remove or check functionality since this will override
        } catch (Exception e) {
            logger.error("Error init to db role permissions");
        }
    }

}
