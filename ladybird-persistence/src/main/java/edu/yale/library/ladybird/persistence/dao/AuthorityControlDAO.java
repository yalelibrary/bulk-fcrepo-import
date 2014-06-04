package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.AuthorityControl;

public interface AuthorityControlDAO extends GenericDAO<AuthorityControl, Integer> {

    AuthorityControl findByAcid(int acid);

}

