package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Roles;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface RolesDAO extends GenericDAO<Roles, Integer> {

    Roles findById(int roleId);

    Roles findByName(String roleName);

}

