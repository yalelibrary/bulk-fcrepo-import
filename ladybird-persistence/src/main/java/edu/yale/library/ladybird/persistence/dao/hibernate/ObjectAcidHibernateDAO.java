package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectAcid;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidDAO;
import org.hibernate.Query;

import java.util.List;

public class ObjectAcidHibernateDAO extends GenericHibernateDAO<ObjectAcid, Integer> implements ObjectAcidDAO {

    @Override
    public ObjectAcid findByOidAndFdid(final int oid, final int fdid) {
        final Query q = getSession().createQuery("from ObjectAcid where objectId = :param1 and fdid =:param2");
        q.setParameter("param1", oid);
        q.setParameter("param2", fdid);
        return q.list().isEmpty() ?  null : (ObjectAcid) q.list().get(0);
    }

    @Override
    public List<ObjectAcid> findListByOidAndFdid(final int oid, final int fdid) {
        final Query q = getSession().createQuery("from ObjectAcid where objectId = :param1 and fdid =:param2");
        q.setParameter("param1", oid);
        q.setParameter("param2", fdid);
        return q.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectAcid> findByOid(int oid) {
        final Query q = getSession().createQuery("from ObjectAcid where objectId = :param1");
        q.setParameter("param1", oid);
        return q.list();
    }

}

