package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectAcidVersion;
import edu.yale.library.ladybird.persistence.dao.ObjectAcidVersionDAO;
import org.hibernate.Query;

public class ObjectAcidVersionHibernateDAO extends GenericHibernateDAO<ObjectAcidVersion, Integer> implements ObjectAcidVersionDAO {

    @Override
    public ObjectAcidVersion findByOidAndFdidAndVersion(final int o, final int fdid, final int version) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ObjectAcidVersion where objectId = :param1 and fdid =:param2 and versionId =:param3");
        q.setParameter("param1", o);
        q.setParameter("param2", fdid);
        q.setParameter("param3", version);
        return q.list().isEmpty() ? null : (ObjectAcidVersion) q.list().get(0); //TODO gets only one. Check business logic.
    }
}

