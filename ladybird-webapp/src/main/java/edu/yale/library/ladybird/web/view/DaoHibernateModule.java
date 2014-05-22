package edu.yale.library.ladybird.web.view;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

import edu.yale.library.ladybird.engine.ProgressEventChangeRecorder;
import edu.yale.library.ladybird.engine.cron.ImportScheduler;
import edu.yale.library.ladybird.engine.cron.ExportScheduler;
import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.kernel.cron.DefaultJobsManager;
import edu.yale.library.ladybird.kernel.cron.JobsManager;
import edu.yale.library.ladybird.persistence.dao.GenericDAO;
import edu.yale.library.ladybird.persistence.dao.CollectionDAO;
import edu.yale.library.ladybird.persistence.dao.ImportFileDAO;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDAO;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import edu.yale.library.ladybird.persistence.dao.FieldMarcMappingDAO;
import edu.yale.library.ladybird.persistence.dao.MonitorDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.UserPreferencesDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlVersionDAO;


import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlVersionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.CollectionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportFileHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportSourceDataHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldDefinitionHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.FieldMarcMappingHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.MonitorHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ProjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserPreferencesHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserProjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserProjectFieldHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserEventHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.PermissionsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesPermissionsHibernateDAO;

import edu.yale.library.ladybird.web.http.CollectionHttpService;
import edu.yale.library.ladybird.web.http.FieldDefinitionHttpService;
import edu.yale.library.ladybird.web.http.ImportSourcerHttpService;
import edu.yale.library.ladybird.web.http.ProjectHttpService;
import edu.yale.library.ladybird.web.http.UserHttpService;
import edu.yale.library.ladybird.web.http.UserEventHttpService;


public class DaoHibernateModule extends AbstractModule {
    @Override
    protected void configure() {
        TypeLiteral<GenericDAO<User, Integer>> userDaoType
                = new TypeLiteral<GenericDAO<User, Integer>>() { };

        TypeLiteral<GenericDAO<Project, Integer>> projectDaoType
                = new TypeLiteral<GenericDAO<Project, Integer>>() { };

        bind(userDaoType).to(UserDAO.class);

        bind(UserDAO.class).to(UserHibernateDAO.class);
        bind(UserPreferencesDAO.class).to(UserPreferencesHibernateDAO.class);
        bind(MonitorDAO.class).to(MonitorHibernateDAO.class);
        bind(ImportSourceDAO.class).to(ImportSourceHibernateDAO.class);
        bind(ImportSourceDataDAO.class).to(ImportSourceDataHibernateDAO.class);
        bind(ImportFileDAO.class).to(ImportFileHibernateDAO.class);
        bind(ProjectDAO.class).to(ProjectHibernateDAO.class);
        bind(CollectionDAO.class).to(CollectionHibernateDAO.class);
        bind(FieldDefinitionDAO.class).to(FieldDefinitionHibernateDAO.class);
        bind(FieldMarcMappingDAO.class).to(FieldMarcMappingHibernateDAO.class);
        bind(ObjectDAO.class).to(ObjectHibernateDAO.class);
        bind(ObjectFileDAO.class).to(ObjectFileHibernateDAO.class);
        bind(UserProjectDAO.class).to(UserProjectHibernateDAO.class);
        bind(UserProjectFieldDAO.class).to(UserProjectFieldHibernateDAO.class);
        bind(UserEventDAO.class).to(UserEventHibernateDAO.class);
        bind(RolesDAO.class).to(RolesHibernateDAO.class);
        bind(PermissionsDAO.class).to(PermissionsHibernateDAO.class);
        bind(RolesPermissionsDAO.class).to(RolesPermissionsHibernateDAO.class);
        bind(AuthorityControlDAO.class).to(AuthorityControlHibernateDAO.class);
        bind(AuthorityControlVersionDAO.class).to(AuthorityControlVersionHibernateDAO.class);

        bind(UserHttpService.class);
        bind(UserEventHttpService.class);
        bind(ImportSourcerHttpService.class);
        bind(ProjectHttpService.class);
        bind(CollectionHttpService.class);
        bind(FieldDefinitionHttpService.class);

        bind(ImportScheduler.class);
        bind(ExportScheduler.class);
        bind(ProgressEventChangeRecorder.class);

        bind(JobsManager.class).to(DefaultJobsManager.class);
    }
}
