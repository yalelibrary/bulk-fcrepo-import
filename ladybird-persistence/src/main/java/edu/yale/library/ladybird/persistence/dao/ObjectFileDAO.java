package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectFile;

import java.util.List;

public interface ObjectFileDAO extends GenericDAO<ObjectFile, Integer> {

    ObjectFile findByOid(int oid);

    List<ObjectFile> findByProject(int projectId);

}

