package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportJobExhead;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class ImportJobExheadHibernateDAO extends GenericHibernateDAO<ImportJobExhead, Integer> implements ImportJobExheadDAO {

    @SuppressWarnings("unchecked")
    public List<ImportJobExhead> findByImportId(final int arg) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("from ImportJobExhead where importId = :param");
            q.setParameter("param", arg);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    public int getNumEntriesPerImportJob(final int arg) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("select count(*) from ImportJobExhead where importId = :param");
            q.setParameter("param", arg);
            return ((Long) q.uniqueResult()).intValue();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

