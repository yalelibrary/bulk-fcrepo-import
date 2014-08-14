package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.hibernate.Query;

import java.util.List;

public class UserEventHibernateDAO extends GenericHibernateDAO<UserEvent, Integer> implements UserEventDAO {

    @Override
    public List<UserEvent> findByUserId(final String userId) {
        final Query q = getSession().createQuery("from UserEvent where userId = :param");
        q.setParameter("param", userId);
        return q.list();
    }

    @Override
    public List<UserEvent> findByEventType(final String eventType) {
        final Query q = getSession().createQuery("from UserEvent where eventType = :param");
        q.setParameter("param", eventType);
        return q.list();
    }

    @Override
    public List<UserEvent> findEventsByUser(final String eventType, final String userId) {
        final Query q = getSession().createQuery("from UserEvent where eventType = :param1 and userId =:param2");
        q.setParameter("param1", eventType);
        q.setParameter("param2", userId);
        return q.list();
    }
}

