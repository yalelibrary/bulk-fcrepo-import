package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectFile;

public interface ObjectFileDAO extends GenericDAO<ObjectFile, Integer> {

    ObjectFile findByOid(int oid);
}

