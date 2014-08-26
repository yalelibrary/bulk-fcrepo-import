package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.RolesPermissions;
import edu.yale.library.ladybird.persistence.dao.RolesPermissionsDAO;
import org.hibernate.Query;

import java.util.List;

public class RolesPermissionsHibernateDAO extends GenericHibernateDAO<RolesPermissions, Integer> implements RolesPermissionsDAO {

    @SuppressWarnings("unchecked")
    @Override
    public RolesPermissions findByRolesPermissionsId(int rolesId, int permissionsId) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.RolesPermissions where roleId = :param1 and permissionsId =:param2");
        q.setParameter("param1", rolesId);
        q.setParameter("param2", permissionsId);
        List<RolesPermissions> list = q.list();
        return (list.isEmpty()) ? null : list.get(0);
    }
}

