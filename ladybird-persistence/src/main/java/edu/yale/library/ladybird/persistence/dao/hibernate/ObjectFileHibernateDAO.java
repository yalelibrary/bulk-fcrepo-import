package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import org.hibernate.Query;

import java.util.List;

public class ObjectFileHibernateDAO extends GenericHibernateDAO<ObjectFile, Integer> implements ObjectFileDAO {


    @Override
    public ObjectFile findByOid(int oid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ObjectFile where oid = :param");
        q.setParameter("param", oid);
        final List<ObjectFile> list = q.list();
        return list.get(0);
    }

}

