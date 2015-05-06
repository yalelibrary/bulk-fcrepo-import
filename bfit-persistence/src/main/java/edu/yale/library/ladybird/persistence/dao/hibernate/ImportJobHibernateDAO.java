package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportJobHibernateDAO extends GenericHibernateDAO<ImportJob, Integer> implements ImportJobDAO {

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportJob> findByUser(int uid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ImportJob where userId = :param1");
            q.setParameter("param1", uid);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportJob> findByJobId(int jobId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ImportJob where importId = :param1");
            q.setParameter("param1", jobId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportJob> findByRequestId(int requestID) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ImportJob where requestId = :param1");
            q.setParameter("param1", requestID);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

