package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectAcidVersion;

public interface ObjectAcidVersionDAO extends GenericDAO<ObjectAcidVersion, Integer> {

    ObjectAcidVersion findByOidAndFdidAndVersion(int oid, int field, int version);

}

