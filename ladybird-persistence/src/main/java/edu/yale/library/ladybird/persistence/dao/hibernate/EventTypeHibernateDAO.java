package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.EventType;
import edu.yale.library.ladybird.entity.ObjectEvent;
import edu.yale.library.ladybird.persistence.dao.EventTypeDAO;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class EventTypeHibernateDAO extends GenericHibernateDAO<EventType, Integer> implements EventTypeDAO {

    private static final Logger logger = getLogger(EventTypeHibernateDAO.class);

    @Override
    public EventType findByEditEvent() {
        final Session s = getSession();
        try {
            Query query = s.createQuery("from UserEditEvent");
            return (EventType) query.list().get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<EventType> findAll() {
        final Session s = getSession();
        final Query q = s.createQuery("from EventType");

        try {
            List<EventType> l = q.list();

            for (EventType o: l) {
                logger.trace("Type={}", o.getObjectEvents().toString());
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

