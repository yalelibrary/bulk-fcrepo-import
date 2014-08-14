package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;

public class RolesPermissionsHibernateDAO extends GenericHibernateDAO<RolesPermissions, Integer> implements RolesPermissionsDAO {

    @Override
    public RolesPermissions findByRolesPermissionsId(int rolesId, int permissionsId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

