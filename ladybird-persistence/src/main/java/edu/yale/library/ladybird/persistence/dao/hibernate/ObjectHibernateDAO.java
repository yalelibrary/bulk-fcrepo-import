package edu.yale.library.ladybird.persistence.dao.hibernate;


import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class ObjectHibernateDAO extends GenericHibernateDAO<Object, Integer> implements ObjectDAO {

    @SuppressWarnings("unchecked")
    @Override
    public edu.yale.library.ladybird.entity.Object findByOid(int oid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.Object where oid = :param");
            q.setParameter("param", oid);
            final List<Object> list = q.list();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<edu.yale.library.ladybird.entity.Object> findByParent(int poid) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.Object where poid = :param");
            q.setParameter("param", poid);
            final List<Object> list = q.list();
            return list;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public int childCount(int oid) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("select count(*) from edu.yale.library.ladybird.entity.Object where poid = :param");
            q.setParameter("param", oid);
            return ((Long) q.uniqueResult()).intValue();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public int projectCount(int projectId) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("select count(*) from edu.yale.library.ladybird.entity.Object where projectId = :param");
            q.setParameter("param", projectId);
            return ((Long) q.uniqueResult()).intValue();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> findByProject(int projectId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.Object where projectId = :param");
            q.setParameter("param", projectId);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

