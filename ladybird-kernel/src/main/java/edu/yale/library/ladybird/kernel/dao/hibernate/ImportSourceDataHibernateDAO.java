


package edu.yale.library.ladybird.kernel.dao.hibernate;

import edu.yale.library.ladybird.kernel.beans.ImportSourceData;
import edu.yale.library.ladybird.kernel.dao.ImportSourceDataDAO;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class ImportSourceDataHibernateDAO extends GenericHibernateDAO<ImportSourceData, Integer> implements ImportSourceDataDAO {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<ImportSourceData>  findByImportId(final int importId) {
        final Query q = getSession().createQuery("from ImportSourceData where importSourceId = :param");
        q.setParameter("param", importId);
        return q.list();
    }

    public List<ImportSourceData>  findByImportId(final int importId, final int row) {
        final Query q = getSession().createQuery("from ImportSourceData where importSourceId = :param1 and zindex = :param2");
        q.setParameter("param1", importId);
        q.setParameter("param2", row);
        return q.list();
    }

}

