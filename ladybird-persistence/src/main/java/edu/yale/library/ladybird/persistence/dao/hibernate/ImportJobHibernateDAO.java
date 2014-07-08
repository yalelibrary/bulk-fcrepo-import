package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.persistence.dao.ImportJobDAO;
import org.hibernate.Query;

import java.util.List;

public class ImportJobHibernateDAO extends GenericHibernateDAO<ImportJob, Integer> implements ImportJobDAO {

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportJob> findByUser(int uid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ImportJob where userId = :param1");
        q.setParameter("param1", uid);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportJob> findByJobId(int jobId) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ImportJob where importId = :param1");
        q.setParameter("param1", jobId);
        return q.list();
    }
}

