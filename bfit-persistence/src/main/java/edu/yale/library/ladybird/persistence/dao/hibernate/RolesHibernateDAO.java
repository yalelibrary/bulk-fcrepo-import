package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.Roles;
import edu.yale.library.ladybird.persistence.dao.RolesDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class RolesHibernateDAO extends GenericHibernateDAO<Roles, Integer> implements RolesDAO {

    @SuppressWarnings("unchecked")
    @Override
    public Roles findById(int roleId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from Roles where roleId = :param");
            q.setParameter("param", roleId);
            final List<Roles> list = q.list();
            return list.get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Roles findByName(String roleName) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from Roles where roleName = :param");
            q.setParameter("param", roleName);
            final List<Roles> list = q.list();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

