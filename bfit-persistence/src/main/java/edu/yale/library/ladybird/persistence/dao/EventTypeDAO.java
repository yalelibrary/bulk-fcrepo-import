package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.EventType;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface EventTypeDAO extends GenericDAO<EventType, Integer> {

    EventType findByEditEvent();

}

