package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.FieldMarcMapping;
import edu.yale.library.ladybird.persistence.dao.FieldMarcMappingDAO;
import org.hibernate.Query;

public class FieldMarcMappingHibernateDAO extends GenericHibernateDAO<FieldMarcMapping, Integer> implements FieldMarcMappingDAO {

    @Override
    public FieldMarcMapping findByFdid(int fdid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.FieldMarcMapping where fdid = :param");
        q.setParameter("param", fdid);
        if (!q.list().isEmpty()) {
            return (FieldMarcMapping) q.list().get(0);
        } else {
            return new FieldMarcMapping();
        }

    }
}

