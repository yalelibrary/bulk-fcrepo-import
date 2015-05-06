package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.UserPreferences;
import edu.yale.library.ladybird.persistence.dao.UserPreferencesDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class UserPreferencesHibernateDAO extends GenericHibernateDAO<UserPreferences, Integer>
        implements UserPreferencesDAO {

    @Override
    public List<UserPreferences> findByUserId(int userId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.UserPreferences "
                    + "where userId = :param");
            q.setParameter("param", userId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

