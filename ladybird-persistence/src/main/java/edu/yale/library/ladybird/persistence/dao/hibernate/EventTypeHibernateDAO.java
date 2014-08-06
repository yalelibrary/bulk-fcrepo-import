package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.EventType;
import edu.yale.library.ladybird.persistence.dao.EventTypeDAO;
import org.hibernate.Query;

public class EventTypeHibernateDAO extends GenericHibernateDAO<EventType, Integer> implements EventTypeDAO {

    @Override
    public EventType findByEditEvent() {
        Query query = getSession().createQuery("from UserEditEvent");
        return (EventType) query.list().get(0);
    }
}

