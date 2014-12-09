package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ImportJobContentsHibernateDAO extends GenericHibernateDAO<ImportJobContents, Integer>
        implements ImportJobContentsDAO {

    private Logger logger = getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    public List<ImportJobContents> findByRow(final int importId, final int row) {
        final Session s = getSession();
        try {
            Query q = s.createQuery("from ImportJobContents where importId= :param1 and row = :param2");
            q.setParameter("param1", importId);
            q.setParameter("param2", row);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<ImportJobContents> findByImportId(final int arg) {
        final Session s = getSession();
        try {
            Query q = s.createQuery("from ImportJobContents where importId = :param");
            q.setParameter("param", arg);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public int getNumEntriesPerImportJob(final int arg) {
        final Session s = getSession();
        try {
            Query q = s.createQuery("select count(*) from edu.yale.library.ladybird.entity.ImportJobContents where importId = :param");
            q.setParameter("param", arg);
            return ((Long) q.uniqueResult()).intValue();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Selects max row number for an import job
     *
     * @param arg importId
     * @return
     */
    public synchronized int getNumRowsPerImportJob(final int arg) {
        Session s = null;
        try {
            s = getSession();
            Query q = s.createQuery("select max(i.row) from edu.yale.library.ladybird.entity.ImportJobContents i where i.importId = :param");
            q.setParameter("param", arg);
            final Object result = q.uniqueResult();

            //FIXME

            if (result == null) {
                logger.error("Hibernate unique result null for param={}", arg);
                return 0;
            }
            return (Integer) result;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

