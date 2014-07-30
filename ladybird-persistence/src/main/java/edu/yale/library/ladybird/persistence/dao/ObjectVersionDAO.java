package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectVersion;

import java.util.List;

public interface ObjectVersionDAO extends GenericDAO<ObjectVersion, Integer> {

    List<ObjectVersion> findByOid(int oid);

    int findMaxVersionByOid(int oid);
}

