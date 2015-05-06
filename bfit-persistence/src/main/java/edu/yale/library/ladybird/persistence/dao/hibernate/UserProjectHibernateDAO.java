package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class UserProjectHibernateDAO extends GenericHibernateDAO<UserProject, Integer> implements UserProjectDAO {

    @Override
    public List<UserProject> findByProjectId(int projectId) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.UserProject "
                    + "where projectId = :param");
            q.setParameter("param", projectId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<UserProject> findByUserId(int userId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.UserProject "
                    + "where userId = :param");
            q.setParameter("param", userId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<UserProject> findByUserAndProject(int userId, int projectId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.UserProject "
                    + "where userId = :param1 and projectId = :param2");
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

