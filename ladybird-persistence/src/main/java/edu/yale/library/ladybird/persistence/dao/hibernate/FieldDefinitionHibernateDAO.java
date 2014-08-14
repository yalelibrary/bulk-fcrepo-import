package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.FieldDefinition;
import edu.yale.library.ladybird.persistence.dao.FieldDefinitionDAO;
import org.hibernate.Query;

import java.util.List;

public class FieldDefinitionHibernateDAO extends GenericHibernateDAO<FieldDefinition, Integer> implements FieldDefinitionDAO {

    /**
     *
     * @param fdid int value of fdid (e.g. 69 or 70)
     * @return FieldDefinition object or null if not found
     */

    @Override
    public FieldDefinition findByFdid(int fdid) {
        final Query q = getSession().createQuery("from FieldDefinition where fdid = :param");
        q.setParameter("param", fdid);
        List<FieldDefinition> list = q.list();
        return list.isEmpty() ? null : list.get(0);
    }
}

