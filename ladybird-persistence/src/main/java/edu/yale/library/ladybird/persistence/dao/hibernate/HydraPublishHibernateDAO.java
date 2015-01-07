package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.HydraPublish;
import edu.yale.library.ladybird.persistence.dao.HydraPublishDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class HydraPublishHibernateDAO extends GenericHibernateDAO<HydraPublish, Integer> implements HydraPublishDAO {

    @Override
    public HydraPublish findByOid(int oid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.HydraPublish where oid = :param");
            q.setParameter("param", oid);
            List<HydraPublish> list = q.list();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

