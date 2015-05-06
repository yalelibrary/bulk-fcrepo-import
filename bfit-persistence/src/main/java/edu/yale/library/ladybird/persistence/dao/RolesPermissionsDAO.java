package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.RolesPermissions;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface RolesPermissionsDAO extends GenericDAO<RolesPermissions, Integer> {

    RolesPermissions findByRolesPermissionsId(int rolesId, int permissionsId);

}

