package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectStringVersion;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ObjectStringVersionDAO extends GenericDAO<ObjectStringVersion, Integer> {

    ObjectStringVersion findByOidAndFdidAndVersion(final int oid, final int fdid, final int version);

    List<ObjectStringVersion> findListByOidAndFdidAndVersion(final int oid, final int fdid, final int version);

}

