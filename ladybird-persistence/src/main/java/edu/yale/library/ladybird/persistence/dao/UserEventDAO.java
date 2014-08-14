package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserEvent;

import java.util.List;


public interface UserEventDAO extends GenericDAO<UserEvent, Integer> {

    List<UserEvent> findByUserId(String userId);

    List<UserEvent> findByEventType(String eventType);

    List<UserEvent> findEventsByUser(String eventType, String userId);
}

