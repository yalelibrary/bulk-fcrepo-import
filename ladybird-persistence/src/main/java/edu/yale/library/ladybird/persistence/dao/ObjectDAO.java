package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Object;

import java.util.List;

public interface ObjectDAO extends GenericDAO<Object, Integer> {

    Object findByOid(int oid);

    List<Object> findByParent(int oid);

    List<Object> findByProject(int projectId);

    int childCount(int oid);

}

