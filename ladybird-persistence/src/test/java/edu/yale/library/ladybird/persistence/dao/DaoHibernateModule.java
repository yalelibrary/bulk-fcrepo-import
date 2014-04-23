package edu.yale.library.ladybird.persistence.dao;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import edu.yale.library.ladybird.kernel.model.Project;
import edu.yale.library.ladybird.kernel.model.User;
import edu.yale.library.ladybird.persistence.dao.hibernate.*;

public class DaoHibernateModule extends AbstractModule {
    @Override
    protected void configure() {
        TypeLiteral<GenericDAO<User, Integer>> userDaoType
                = new TypeLiteral<GenericDAO<User, Integer>>() { };

        TypeLiteral<GenericDAO<Project, Integer>> projectDaoType
                = new TypeLiteral<GenericDAO<Project, Integer>>() { };

        bind(userDaoType).to(UserDAO.class);
        bind(UserDAO.class).to(UserHibernateDAO.class);
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
    }
}
