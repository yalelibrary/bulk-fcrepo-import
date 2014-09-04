package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportSourceData;
import edu.yale.library.ladybird.persistence.dao.ImportSourceDataDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ImportSourceDataHibernateDAO extends GenericHibernateDAO<ImportSourceData, Integer> implements ImportSourceDataDAO {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<ImportSourceData> findByImportId(final int importId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from ImportSourceData where importSourceId = :param");
            q.setParameter("param", importId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    public List<ImportSourceData> findByImportIdAndLocalIdentifier(final int importId, final String localid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ImportSourceData where importSourceId = :param1 and localidentifier = :param2");
            q.setParameter("param1", importId);
            q.setParameter("param2", localid);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

