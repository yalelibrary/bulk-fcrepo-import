package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class AuthorityControlHibernateDAO extends GenericHibernateDAO<AuthorityControl, Integer> implements AuthorityControlDAO {

    @Override
    public AuthorityControl findByAcid(int acid) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.AuthorityControl "
                    + "where acid = :param");
            q.setParameter("param", acid);
            return (AuthorityControl) q.list().get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AuthorityControl> findByFdid(int fdid) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.AuthorityControl "
                    + "where fdid = :param");
            q.setParameter("param", fdid);
            return (List<AuthorityControl>) q.list();
        } finally {
            if (s != null) {
                s.close();
            }

        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public int countByFdid(int fdid) {
        final Session s = getSession();
        try {
            Query q = s.createQuery("select count(*) from edu.yale.library.ladybird.entity.AuthorityControl where fdid = :param");
            q.setParameter("param", fdid);
            return ((Long) q.uniqueResult()).intValue();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AuthorityControl> findByFdidAndStringValue(int fd, String val) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.AuthorityControl where fdid = :param1 and value =:param2");
            q.setParameter("param1", fd);
            q.setParameter("param2", val);
            return q.list();
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

