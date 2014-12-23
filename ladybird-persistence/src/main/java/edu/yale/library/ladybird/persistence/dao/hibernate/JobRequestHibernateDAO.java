package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.JobRequest;
import edu.yale.library.ladybird.persistence.dao.JobRequestDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class JobRequestHibernateDAO extends GenericHibernateDAO<JobRequest, Integer> implements JobRequestDAO {

    @SuppressWarnings("unchecked")
    @Override
    public List<JobRequest> findByUserAndProject(int userId, int projectId) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.JobRequest where currentUserId = :param1 and currentProjectId =:param2");
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