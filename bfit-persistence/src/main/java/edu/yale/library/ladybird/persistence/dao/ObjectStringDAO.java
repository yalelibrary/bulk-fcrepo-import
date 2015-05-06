package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ObjectString;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ObjectStringDAO extends GenericDAO<ObjectString, Integer> {

    ObjectString findByOidAndFdid(final int oid, final int fdid);

    List<ObjectString> findListByOidAndFdid(final int oid, final int fdid);

    List<ObjectString> findByOid(final int oid);

}

