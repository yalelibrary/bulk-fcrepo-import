package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.AuthorityControl;
import edu.yale.library.ladybird.persistence.dao.AuthorityControlDAO;
import org.hibernate.Query;

public class AuthorityControlHibernateDAO extends GenericHibernateDAO<AuthorityControl, Integer> implements AuthorityControlDAO {

    @Override
    public AuthorityControl findByAcid(int acid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.AuthorityControl "
                + "where acid = :param");
        q.setParameter("param", acid);
        return (AuthorityControl) q.list().get(0);
    }

}

