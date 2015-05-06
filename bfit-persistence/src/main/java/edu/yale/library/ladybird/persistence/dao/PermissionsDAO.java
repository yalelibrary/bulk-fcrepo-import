package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Permissions;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface PermissionsDAO extends GenericDAO<Permissions, Integer> {

    Permissions findById(int permissionsId);

    Permissions findByName(String label);

}

