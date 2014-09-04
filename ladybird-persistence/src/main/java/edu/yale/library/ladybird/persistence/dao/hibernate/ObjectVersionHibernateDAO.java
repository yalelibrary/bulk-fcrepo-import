package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class ObjectVersionHibernateDAO extends GenericHibernateDAO<ObjectVersion, Integer> implements ObjectVersionDAO {

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectVersion> findByOid(int oid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectVersion where oid = :param");
            q.setParameter("param", oid);
            final List<ObjectVersion> list = q.list();
            return list;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Returns last row object
     *
     * @param oid object id
     * @return List<ObjectVersion>
     */
    @SuppressWarnings("unchecked")
    @Override
    public int findMaxVersionByOid(int oid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("select max(o.versionId)from edu.yale.library.ladybird.entity.ObjectVersion o where oid = :param");
            q.setParameter("param", oid);
            return ((Integer) q.uniqueResult());
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

