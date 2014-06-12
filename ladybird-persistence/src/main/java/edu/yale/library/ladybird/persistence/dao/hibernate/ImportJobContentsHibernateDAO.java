package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportJobContents;
import edu.yale.library.ladybird.persistence.dao.ImportJobContentsDAO;
import org.hibernate.Query;

import java.util.List;

public class ImportJobContentsHibernateDAO extends GenericHibernateDAO<ImportJobContents, Integer>
        implements ImportJobContentsDAO {

    @SuppressWarnings("unchecked")
    public List<ImportJobContents> findByRow(final int importId, final int row) {
        Query q = getSession().createQuery("from ImportJobContents where importId= :param1 and row = :param2");
        q.setParameter("param1", importId);
        q.setParameter("param2", row);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<ImportJobContents> findByImportId(final int arg) {
        Query q = getSession().createQuery("from ImportJobContents where importId = :param");
        q.setParameter("param", arg);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public int getNumEntriesPerImportJob(final int arg) {
        Query q = getSession().createQuery("select count(*) from ImportJobContents where importId = :param");
        q.setParameter("param", arg);
        return ((Long) q.uniqueResult()).intValue();
    }

    /**
     * Selects max row number for an import job
     *
     * @param arg
     * @return
     */
    public int getNumRowsPerImportJob(final int arg) {
        Query q = getSession().createQuery("select max(i.row) from ImportJobContents i where importId = :param");
        q.setParameter("param", arg);
        return ((Integer) q.uniqueResult());
    }
}

