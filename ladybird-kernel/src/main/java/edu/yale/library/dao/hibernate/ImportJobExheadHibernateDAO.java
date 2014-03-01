


package edu.yale.library.dao.hibernate;

import edu.yale.library.beans.ImportJobContents;
import edu.yale.library.beans.ImportJobExhead;
import edu.yale.library.dao.ImportJobExheadDAO;
import org.hibernate.Query;

import java.util.List;

public class ImportJobExheadHibernateDAO extends GenericHibernateDAO<ImportJobExhead, Integer> implements ImportJobExheadDAO {
    public List<ImportJobContents> findByImportId(final int arg) {
        Query q = getSession().createQuery("from ImportJobExhead where importId = :param");
        q.setParameter("param", arg);
        return q.list();
    }

    public int getNumEntriesPerImportJob(final int arg) {
        Query q = getSession().createQuery("select count(*) from ImportJobExhead where importId = :param");
        q.setParameter("param", arg);
        return ((Long) q.uniqueResult()).intValue();
    }

}

