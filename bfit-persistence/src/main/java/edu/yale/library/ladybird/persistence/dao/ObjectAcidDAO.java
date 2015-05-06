package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectAcid;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ObjectAcidDAO extends GenericDAO<ObjectAcid, Integer> {

    ObjectAcid findByOidAndFdid(int oid, int field);

    List<ObjectAcid> findListByOidAndFdid(final int oid, final int fdid);

    List<ObjectAcid> findByOid(int oid);

}

