package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import org.hibernate.Query;

import java.util.List;

public class SettingsHibernateDAO extends GenericHibernateDAO<Settings, Integer> implements SettingsDAO {

    @Override
    public Settings findById(int rowId) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.Settings where id = :param");
        q.setParameter("param", rowId);
        final List<Settings> list = q.list();
        return list.get(0);
    }

    @Override
    public Settings findByProperty(String property) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.Settings where property = :param");
        q.setParameter("param", property);
        final List<Settings> list = q.list();
        return list.isEmpty() ? null : list.get(0);
    }
}

