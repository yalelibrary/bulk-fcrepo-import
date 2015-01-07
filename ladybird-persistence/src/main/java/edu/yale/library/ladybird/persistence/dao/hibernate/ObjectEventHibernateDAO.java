package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectEvent;
import edu.yale.library.ladybird.persistence.dao.ObjectEventDAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ObjectEventHibernateDAO extends GenericHibernateDAO<ObjectEvent, Integer> implements ObjectEventDAO {

    private Logger logger = getLogger(ObjectEventHibernateDAO.class);

    @Override
    public List<ObjectEvent> findByUserAndOid(int userId, int oid) {
        final Session s = getSession();

        try {
            Query query = s.createQuery("from ObjectEvent o where userId =:param and oid =:param2");
            query.setParameter("param", userId);
            query.setParameter("param2", oid);
            return query.list().isEmpty() ? null : query.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }


    @SuppressWarnings("unchecked")
    public List<ObjectEvent> findAll() {
        final Session s = getSession();
        final Query q = s.createQuery("from ObjectEvent");

        try {
            List<ObjectEvent> l = q.list();

            for (ObjectEvent o: l) {
                logger.trace(o.getEventType().getObjectEvents().toString());
            }
            return l;
        } catch (HibernateException e) {
            logger.error("Error finding all", e);
            return Collections.emptyList();
        } finally {
            try {
                s.close();
            } catch (HibernateException e) {
                logger.error("Error closing hibernate session");
            }

        }
    }

}

