package edu.yale.library.ladybird;

import edu.yale.library.ladybird.engine.EventBus;
import edu.yale.library.ladybird.engine.oai.FdidMarcMappingUtil;
import edu.yale.library.ladybird.entity.event.RollbackEventType;
import edu.yale.library.ladybird.entity.event.UserEditEvent;
import edu.yale.library.ladybird.kernel.ApplicationBootstrap;
import edu.yale.library.ladybird.kernel.GuiceModule;
import edu.yale.library.ladybird.persistence.dao.EventTypeDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.EventTypeHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import org.slf4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.slf4j.LoggerFactory.getLogger;


public class AppContextListener implements ServletContextListener {

    private static final Logger logger = getLogger(AppContextListener.class);

    private final ApplicationBootstrap applicationBootstrap = new ApplicationBootstrap();

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        applicationBootstrap.stop();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        applicationBootstrap.setGuiceModule(new GuiceModule());
        applicationBootstrap.init();
        EventBus eventBus = getExportBus();
        eventBus.init();

        // Load initial fdid marc mappings
        try {
            FdidMarcMappingUtil fdidMarcUtil = new FdidMarcMappingUtil();
            fdidMarcUtil.setFieldMarcMappingDAO(new FieldMarcMappingHibernateDAO()); //FIXME
            fdidMarcUtil.setInitialFieldMarcDb();
        } catch (Exception e) {
            logger.error("Error in fdid marc init", e);
        }

        //Load properties file
        try {
            SettingsInit settingsInit = new SettingsInit();
            settingsInit.loadAndStore();
        } catch (Exception e) {
            logger.error("Error in setting settings", e);
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

        //Add role permissions, if empty
        try {
            RolesPermissionsInit rolesPermissionsInit = new RolesPermissionsInit();
            rolesPermissionsInit.load();
        } catch (Exception e) {
            logger.error("Error init to db role permissions");
        }
    }

    private EventBus getExportBus() {
        return new EventBus();
    }

}
