package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectVersion;
import edu.yale.library.ladybird.persistence.dao.ObjectVersionDAO;
import org.hibernate.Query;

import java.util.List;

public class ObjectVersionHibernateDAO extends GenericHibernateDAO<ObjectVersion, Integer> implements ObjectVersionDAO {

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectVersion> findByOid(int oid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ObjectVersion where oid = :param");
        q.setParameter("param", oid);
        final List<ObjectVersion> list = q.list();
        return list;
    }

    /**
     * Returns last row object
     * @param oid object id
     * @return List<ObjectVersion>
     */
    @SuppressWarnings("unchecked")
    @Override
    public int findMaxVersionByOid(int oid) {
        final Query q = getSession().createQuery("select max(o.versionId)from edu.yale.library.ladybird.entity.ObjectVersion o where oid = :param");
        q.setParameter("param", oid);
        return ((Integer) q.uniqueResult());
    }
}

