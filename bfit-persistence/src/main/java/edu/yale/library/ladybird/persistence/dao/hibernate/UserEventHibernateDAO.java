package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class UserEventHibernateDAO extends GenericHibernateDAO<UserEvent, Integer> implements UserEventDAO {

    @Override
    public List<UserEvent> findByUserId(final String userId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from UserEvent where userId = :param");
            q.setParameter("param", userId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<UserEvent> findByEventType(final String eventType) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from UserEvent where eventType = :param");
            q.setParameter("param", eventType);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<UserEvent> findEventsByUser(final String eventType, final String userId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from UserEvent where eventType = :param1 and userId =:param2");
            q.setParameter("param1", eventType);
            q.setParameter("param2", userId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

