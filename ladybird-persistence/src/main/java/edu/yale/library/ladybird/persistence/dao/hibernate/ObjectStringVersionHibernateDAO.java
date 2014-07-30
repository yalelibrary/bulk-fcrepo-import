package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import org.hibernate.Query;

public class ObjectStringVersionHibernateDAO extends GenericHibernateDAO<ObjectStringVersion, Integer> implements ObjectStringVersionDAO {

    @Override
    public ObjectStringVersion findByOidAndFdidAndVersion(final int o, final int fdid, final int version) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ObjectStringVersion where oid = :param1 and fdid =:param2 and versionId =:param3");
        q.setParameter("param1", o);
        q.setParameter("param2", fdid);
        q.setParameter("param3", version);
        return q.list().isEmpty() ? null : (ObjectStringVersion) q.list().get(0); //TODO gets only one. Check business logic.
    }

}

