package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.UserProjectField;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class UserProjectFieldHibernateDAO extends GenericHibernateDAO<UserProjectField, Integer>
        implements UserProjectFieldDAO {

    @Override
    public List<UserProjectField> findByUserAndProject(final int userId, final int projectId) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from UserProjectField where userId = :param1 and projectId =:param2");
            q.setParameter("param1", userId);
            q.setParameter("param2", projectId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

