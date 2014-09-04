package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectStringVersion;
import edu.yale.library.ladybird.persistence.dao.ObjectStringVersionDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class ObjectStringVersionHibernateDAO extends GenericHibernateDAO<ObjectStringVersion, Integer> implements ObjectStringVersionDAO {

    //TODO used by Rollbacker. Remove
    @Override
    public ObjectStringVersion findByOidAndFdidAndVersion(final int o, final int fdid, final int version) {
        final Session s = getSession();


        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectStringVersion where oid = :param1 and fdid =:param2 and versionId =:param3");
            q.setParameter("param1", o);
            q.setParameter("param2", fdid);
            q.setParameter("param3", version);
            return q.list().isEmpty() ? null : (ObjectStringVersion) q.list().get(0); //TODO gets only one. Check business logic.
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectStringVersion> findListByOidAndFdidAndVersion(final int o, final int fdid, final int version) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectStringVersion where oid = :param1 and fdid =:param2 and versionId =:param3");
            q.setParameter("param1", o);
            q.setParameter("param2", fdid);
            q.setParameter("param3", version);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

