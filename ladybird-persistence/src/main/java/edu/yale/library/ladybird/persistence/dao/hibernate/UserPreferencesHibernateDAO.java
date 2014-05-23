package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.UserPreferences;
import edu.yale.library.ladybird.entity.UserProject;
import edu.yale.library.ladybird.persistence.dao.UserPreferencesDAO;
import org.hibernate.Query;

import java.util.List;

public class UserPreferencesHibernateDAO extends GenericHibernateDAO<UserPreferences, Integer>
        implements UserPreferencesDAO {

    @Override
    public List<UserPreferences> findByUserId(int userId) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.UserPreferences "
                + "where userId = :param");
        q.setParameter("param", userId);
        return q.list();
    }

}

