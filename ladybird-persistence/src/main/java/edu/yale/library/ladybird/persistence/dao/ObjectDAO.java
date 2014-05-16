package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Object;

public interface ObjectDAO extends GenericDAO<Object, Integer> {

    edu.yale.library.ladybird.entity.Object findByOid(int oid);

}

