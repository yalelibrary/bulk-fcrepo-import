package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.kernel.model.User;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.hibernate.Query;

import java.util.List;

public class UserHibernateDAO extends GenericHibernateDAO<User, Integer> implements UserDAO {

    @Override
    public List<User> findByEmail(String email) {
        final Query q = getSession().createQuery("from User where email = :param");
        q.setParameter("param", email);
        return q.list();
    }

}