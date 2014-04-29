


package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.entity.model.Project;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import org.hibernate.Query;

import java.util.List;

public class ProjectHibernateDAO extends GenericHibernateDAO<Project, Integer> implements ProjectDAO {

    public List<Project> findByLabel(String label) {
        final Query q = getSession().createQuery("from Project where label = :param");
        q.setParameter("param", label);
        return q.list();
    }

}

