package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import org.hibernate.Query;

import java.util.List;

public class ObjectHibernateDAO extends GenericHibernateDAO<Object, Integer> implements ObjectDAO {


    @Override
    public edu.yale.library.ladybird.entity.Object findByOid(int oid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.Object where oid = :param");
        q.setParameter("param", oid);
        final List<Object> list = q.list();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<edu.yale.library.ladybird.entity.Object> findByParent(int poid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.Object where poid = :param");
        q.setParameter("param", poid);
        final List<Object> list = q.list();
        return list;
    }

    @Override
    public int childCount(int oid) {
        Query q = getSession().createQuery("select count(*) from edu.yale.library.ladybird.entity.Object where poid = :param");
        q.setParameter("param", oid);
        return ((Long) q.uniqueResult()).intValue();
    }
}

