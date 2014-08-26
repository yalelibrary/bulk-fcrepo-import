package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.Permissions;

import edu.yale.library.ladybird.persistence.dao.PermissionsDAO;
import org.hibernate.Query;

import java.util.List;

public class PermissionsHibernateDAO extends GenericHibernateDAO<Permissions, Integer> implements PermissionsDAO {

    @SuppressWarnings("unchecked")
    @Override
    public Permissions findById(int roleId) {
        final Query q = getSession().createQuery("from Permissions where permissionsId = :param");
        q.setParameter("param", roleId);
        final List<Permissions> list = q.list();
        return list.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Permissions findByName(String label) {
        final Query q = getSession().createQuery("from Permissions where permissionsName = :param");
        q.setParameter("param", label);
        final List<Permissions> list = q.list();
        return list.get(0);
    }
}

