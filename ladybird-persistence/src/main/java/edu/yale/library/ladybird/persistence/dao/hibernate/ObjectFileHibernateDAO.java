package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.entity.Object;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ObjectFileHibernateDAO extends GenericHibernateDAO<ObjectFile, Integer> implements ObjectFileDAO {

    private Logger logger = getLogger(ObjectFileHibernateDAO.class);

    @SuppressWarnings("unchecked")
    @Override
    public ObjectFile findByOid(int oid) {
        final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ObjectFile where oid = :param");
        q.setParameter("param", oid);
        final List<ObjectFile> list = q.list();
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Returns by project. Note that the tables are not linked. Ignores exception
     * @param projectId project id
     * @return list of ojbectfile or empty list
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectFile> findByProject(int projectId) {
        try {
            final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.Object where projectId = :param");
            q.setParameter("param", projectId);
            final List<Object> list = q.list();

            final List<ObjectFile> objFileList = new ArrayList<>();

            for (Object o: list) {
                ObjectFile objectFile = findByOid(o.getOid());
                if (objectFile == null) {
                    logger.debug("No object file for oid={}", o.getOid());
                    continue;
                }
                objFileList.add(findByOid(o.getOid()));
            }
            return objFileList;
        } catch (HibernateException e) {
            logger.error("Error={}", e);
            return Collections.emptyList();
        }
    }
}

