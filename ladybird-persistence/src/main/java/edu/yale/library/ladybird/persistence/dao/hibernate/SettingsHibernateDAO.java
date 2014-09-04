package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class SettingsHibernateDAO extends GenericHibernateDAO<Settings, Integer> implements SettingsDAO {

    @Override
    public Settings findById(int rowId) {
        final Session session = getSession();
        try {
            final Query q = session.createQuery("from edu.yale.library.ladybird.entity.Settings where id = :param");
            q.setParameter("param", rowId);
            final List<Settings> list = q.list();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Settings findByProperty(String property) {
        final Session session = getSession();
        try {
            final Query q = session.createQuery("from edu.yale.library.ladybird.entity.Settings where property = :param");
            q.setParameter("param", property);
            final List<Settings> list = q.list();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

