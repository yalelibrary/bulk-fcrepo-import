package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class UserHibernateDAO extends GenericHibernateDAO<User, Integer> implements UserDAO {

    private Logger logger = getLogger(this.getClass());

    public List<User> findByEmail(final String email) {
        Session s = getSession();
        final Query q = s.createQuery("from User where email = :param");
        q.setParameter("param", email);
        List list = q.list();

        try {
            s.close();
        } catch (HibernateException e) {
            logger.error("Error closing session", e);
        }

        return list;
    }

    @Override
    public List<User> findByUsername(final String netid) {
        Session s = getSession();
        try {
            final Query q = s.createQuery("from User where username = :param");
            q.setParameter("param", netid);
            return q.list();
        } catch (HibernateException e) {
            throw e;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (HibernateException e) {
                    logger.error("Error closing session", e);
                }
            }
        }
    }

    @Override
    public String findUsernameByUserId(int field) {
        Session s = getSession();
        try {
            final Query q = s.createQuery("from User where userId = :param");
            q.setParameter("param", field);
            final List<User> userList = q.list();
            return userList.isEmpty() ? null : userList.get(0).getUsername();
        } catch (HibernateException e) {
            throw e;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (HibernateException e) {
                    logger.error("Error closing session", e);
                }
            }
        }
    }

    @Override
    public User findByUserId(int field) {
        Session s = getSession();
        try {
            final Query q = s.createQuery("from User where userId = :param");
            q.setParameter("param", field);
            final List<User> userList = q.list();
            return userList.get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<String> getEmails() {
        Session s = getSession();
        try {
            final Query q = s.createQuery("select u.email from User u");
            final List<String> userList = q.list();
            return userList;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<String> getUsernames() {
        Session s = getSession();
        try {
            final Query q = s.createQuery("select u.username from User u");
            final List<String> userList = q.list();
            return userList;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}