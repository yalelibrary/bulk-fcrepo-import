package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.AuthorityControl;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface AuthorityControlDAO extends GenericDAO<AuthorityControl, Integer> {

    AuthorityControl findByAcid(int acid);

    List<AuthorityControl> findByFdid(int fdid);

    int countByFdid(int fdid);

    List<AuthorityControl> findByFdidAndStringValue(int fdid, String value);

}

