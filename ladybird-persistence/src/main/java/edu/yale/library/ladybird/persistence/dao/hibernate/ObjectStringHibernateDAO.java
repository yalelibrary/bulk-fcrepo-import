package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectString;
import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import org.hibernate.Query;

public class ObjectStringHibernateDAO extends GenericHibernateDAO<ObjectString, Integer> implements ObjectStringDAO {

    @Override
    public ObjectString findByOidAndFdid(final int o, final int fdid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ObjectString where oid = :param1 and fdid =:param2");
        q.setParameter("param1", o);
        q.setParameter("param2", fdid);
        return q.list().isEmpty() ? null : (ObjectString) q.list().get(0); //TODO gets only one. Check business logic.
    }

}

