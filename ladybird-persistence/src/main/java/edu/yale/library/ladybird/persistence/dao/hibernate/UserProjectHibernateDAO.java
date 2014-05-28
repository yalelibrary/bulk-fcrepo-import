package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.persistence.dao.UserProjectDAO;
import org.hibernate.Query;

import java.util.List;

public class UserProjectHibernateDAO extends GenericHibernateDAO<UserProject, Integer> implements UserProjectDAO {

    @Override
    public List<UserProject> findByProjectId(int projectId) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.UserProject "
                + "where projectId = :param");
        q.setParameter("param", projectId);
        return q.list();
    }

    @Override
    public List<UserProject> findByUserId(int userId) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.UserProject "
                + "where userId = :param");
        q.setParameter("param", userId);
        return q.list();
    }

    @Override
    public List<UserProject> findByUserAndProject(int userId, int projectId) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.UserProject "
                + "where userId = :param1 and projectId = :param2");
        q.setParameter("param1", userId);
        q.setParameter("param2", projectId);
        return q.list();
    }
}

