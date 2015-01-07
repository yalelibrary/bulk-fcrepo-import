package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ProjectTemplate;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class ProjectTemplateHibernateDAO extends GenericHibernateDAO<ProjectTemplate, Integer>
        implements ProjectTemplateDAO {

    public int getCountByLabel(final String arg) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("select count(*) from ProjectTemplate where label = :param");
            q.setParameter("param", arg);
            return ((Long) q.uniqueResult()).intValue();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<ProjectTemplate> findByProjectId(int arg) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("from ProjectTemplate where projectId = :param1");
            q.setParameter("param1", arg);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public ProjectTemplate findByLabel(String label) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("from ProjectTemplate where label = :param1");
            q.setParameter("param1", label);
            return (q.list().isEmpty()) ? null : (ProjectTemplate) q.list().get(0);
        } finally {
            if (s != null) {
                s.close();
            }

        }
    }
}

