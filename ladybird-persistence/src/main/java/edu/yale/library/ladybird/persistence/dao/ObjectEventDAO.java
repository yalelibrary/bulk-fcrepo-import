package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectEvent;

import java.util.List;

public interface ObjectEventDAO extends GenericDAO<ObjectEvent, Integer> {

    List<ObjectEvent> findByUserAndOid(int userId, int oid);

}

