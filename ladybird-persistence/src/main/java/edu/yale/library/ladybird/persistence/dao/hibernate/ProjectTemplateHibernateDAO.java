package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateDAO;
import org.hibernate.Query;

import java.util.List;

public class ProjectTemplateHibernateDAO extends GenericHibernateDAO<ProjectTemplate, Integer>
        implements ProjectTemplateDAO {

    public int getCountByLabel(final String arg) {
        Query q = getSession().createQuery("select count(*) from ProjectTemplate where label = :param");
        q.setParameter("param", arg);
        return ((Long) q.uniqueResult()).intValue();
    }

    @Override
    public List<ProjectTemplate> findByProjectId(int arg) {
        Query q = getSession().createQuery("from ProjectTemplate where projectId = :param1");
        q.setParameter("param1", arg);
        return q.list();
    }

    @Override
    public ProjectTemplate findByLabel(String label) {
        Query q = getSession().createQuery("from ProjectTemplate where label = :param1");
        q.setParameter("param1", label);
        return (q.list().isEmpty()) ? null : (ProjectTemplate) q.list().get(0);
    }
}

