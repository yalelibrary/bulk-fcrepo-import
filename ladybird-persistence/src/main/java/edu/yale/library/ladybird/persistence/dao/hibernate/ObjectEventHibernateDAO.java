package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectEvent;
import edu.yale.library.ladybird.persistence.dao.ObjectEventDAO;
import org.hibernate.Query;

import java.util.List;

public class ObjectEventHibernateDAO extends GenericHibernateDAO<ObjectEvent, Integer> implements ObjectEventDAO {

    @Override
    public List<ObjectEvent> findByUserAndOid(int userId, int oid) {
        Query query = getSession().createQuery("from ObjectEvent o where userId =:param and oid =:param2");
        query.setParameter("param", userId);
        query.setParameter("param2", oid);
        return query.list().isEmpty() ? null : query.list();
    }
}

