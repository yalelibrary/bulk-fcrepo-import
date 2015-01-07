package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class ObjectStringHibernateDAO extends GenericHibernateDAO<ObjectString, Integer> implements ObjectStringDAO {

    @Override
    public ObjectString findByOidAndFdid(final int o, final int fdid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectString where oid = :param1 and fdid =:param2");
            q.setParameter("param1", o);
            q.setParameter("param2", fdid);
            return q.list().isEmpty() ? null : (ObjectString) q.list().get(0); //TODO gets only one. Check business logic.
        }  finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<ObjectString> findListByOidAndFdid(final int o, final int fdid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectString where oid = :param1 and fdid =:param2");
            q.setParameter("param1", o);
            q.setParameter("param2", fdid);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectString> findByOid(final int o) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectString where oid = :param1");
            q.setParameter("param1", o);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

