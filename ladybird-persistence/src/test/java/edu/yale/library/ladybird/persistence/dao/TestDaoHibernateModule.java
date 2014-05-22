package edu.yale.library.ladybird.persistence.dao;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.entity.User;
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
import edu.yale.library.ladybird.persistence.dao.hibernate.UserEventHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserProjectHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.PermissionsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.RolesPermissionsHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.AuthorityControlVersionHibernateDAO;




public class TestDaoHibernateModule extends AbstractModule {
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
        bind(ImportFileDAO.class).to(ImportFileHibernateDAO.class);
        bind(ImportSourceDAO.class).to(ImportSourceHibernateDAO.class);
        bind(ImportSourceDataDAO.class).to(ImportSourceDataHibernateDAO.class);
        bind(ProjectDAO.class).to(ProjectHibernateDAO.class);
        bind(CollectionDAO.class).to(CollectionHibernateDAO.class);
        bind(FieldDefinitionDAO.class).to(FieldDefinitionHibernateDAO.class);
        bind(FieldMarcMappingDAO.class).to(FieldMarcMappingHibernateDAO.class);
        bind(ObjectDAO.class).to(ObjectHibernateDAO.class);
        bind(ObjectFileDAO.class).to(ObjectFileHibernateDAO.class);
        bind(UserEventDAO.class).to(UserEventHibernateDAO.class);
        bind(UserProjectDAO.class).to(UserProjectHibernateDAO.class);
        bind(RolesDAO.class).to(RolesHibernateDAO.class);
        bind(PermissionsDAO.class).to(PermissionsHibernateDAO.class);
        bind(RolesPermissionsDAO.class).to(RolesPermissionsHibernateDAO.class);
        bind(AuthorityControlDAO.class).to(AuthorityControlHibernateDAO.class);
        bind(AuthorityControlVersionDAO.class).to(AuthorityControlVersionHibernateDAO.class);
    }
}
