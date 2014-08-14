package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.Roles;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import org.hibernate.Query;

import java.util.List;

public class RolesHibernateDAO extends GenericHibernateDAO<Roles, Integer> implements RolesDAO {

    @Override
    public Roles findById(int roleId) {
        final Query q = getSession().createQuery("from Roles where roleId = :param");
        q.setParameter("param", roleId);
        final List<Roles> list = q.list();
        return list.get(0);
    }

}

