package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.EventType;
import edu.yale.library.ladybird.persistence.dao.EventTypeDAO;
import org.hibernate.Query;
import org.hibernate.Session;

public class EventTypeHibernateDAO extends GenericHibernateDAO<EventType, Integer> implements EventTypeDAO {

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
}

