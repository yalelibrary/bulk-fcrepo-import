package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.hibernate.Query;

import java.util.List;

public class UserHibernateDAO extends GenericHibernateDAO<User, Integer> implements UserDAO {

    @Override
    public List<User> findByEmail(final String email) {
        final Query q = getSession().createQuery("from User where email = :param");
        q.setParameter("param", email);
        return q.list();
    }

    @Override
    public List<User> findByUsername(final String netid) {
        final Query q = getSession().createQuery("from User where username = :param");
        q.setParameter("param", netid);
        return q.list();
    }

    @Override
    public String findByUserId(int field) {
        final Query q = getSession().createQuery("from User where userId = :param");
        q.setParameter("param", field);
        final List<User> userList = q.list();
        return userList.get(0).getUsername();
    }
}