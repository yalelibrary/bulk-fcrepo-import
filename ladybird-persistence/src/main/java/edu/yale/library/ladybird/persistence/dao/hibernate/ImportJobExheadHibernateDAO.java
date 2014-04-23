


package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.kernel.model.ImportJobExhead;
import edu.yale.library.ladybird.persistence.dao.ImportJobExheadDAO;
import org.hibernate.Query;

import java.util.List;

public class ImportJobExheadHibernateDAO extends GenericHibernateDAO<ImportJobExhead, Integer> implements ImportJobExheadDAO {

    @SuppressWarnings("unchecked")
    public List<ImportJobExhead> findByImportId(final int arg) {
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

