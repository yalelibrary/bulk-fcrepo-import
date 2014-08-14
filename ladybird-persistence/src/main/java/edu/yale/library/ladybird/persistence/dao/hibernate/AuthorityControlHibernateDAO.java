package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import org.hibernate.Query;

import java.util.List;

public class AuthorityControlHibernateDAO extends GenericHibernateDAO<AuthorityControl, Integer> implements AuthorityControlDAO {

    @Override
    public AuthorityControl findByAcid(int acid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.AuthorityControl "
                + "where acid = :param");
        q.setParameter("param", acid);
        return (AuthorityControl) q.list().get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AuthorityControl> findByFdid(int fdid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.AuthorityControl "
                + "where fdid = :param");
        q.setParameter("param", fdid);
        return (List<AuthorityControl>) q.list();
    }


    @SuppressWarnings("unchecked")
    @Override
    public int countByFdid(int fdid) {
        Query q = getSession().createQuery("select count(*) from edu.yale.library.ladybird.entity.AuthorityControl where fdid = :param");
        q.setParameter("param", fdid);
        return ((Long) q.uniqueResult()).intValue();
    }

}

