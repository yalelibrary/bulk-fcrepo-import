package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.Object;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ObjectFileHibernateDAO extends GenericHibernateDAO<ObjectFile, Integer> implements ObjectFileDAO {

    private Logger logger = getLogger(ObjectFileHibernateDAO.class);

    @SuppressWarnings("unchecked")
    @Override
    public ObjectFile findByOid(final int oid) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectFile where oid = :param");
            q.setParameter("param", oid);
            final List<ObjectFile> list = q.list();

            if (list.isEmpty()) {
                logger.trace("Empty list for oid={}", oid);
            }
            return list.isEmpty() ? null : list.get(0);
        } finally {
            if (s != null) {
                s.close();
            } else {
                logger.error("Session null");
            }
        }
    }

    /**
     * Returns by project. Note that the tables are not linked. Ignores exception
     *
     * @param projectId project id
     * @return list of ojbectfile or empty list
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectFile> findByProject(int projectId) {
        Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.Object where projectId = :param");
            q.setParameter("param", projectId);
            final List<Object> list = q.list();

            s.close();

            final List<ObjectFile> objFileList = new ArrayList<>();

            for (final Object o : list) {
                final ObjectFile objectFile = findByOid(o.getOid());
                if (objectFile == null) {
                    logger.trace("No object file for oid={}", o.getOid());
                    continue;
                }
                objFileList.add(findByOid(o.getOid()));
            }
            return objFileList;
        } catch (HibernateException e) {
            logger.error("Error", e);
            return Collections.emptyList();
        }
    }


    /**
     * Returns by project. Note that the tables are not linked. Ignores exception
     *
     * @param projectId project id
     * @return list of ojbectfile or empty list
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ObjectFile> findByProjectMax(int projectId, int startRow, int count) {
        Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.Object where projectId = :param");
            q.setParameter("param", projectId);
            q.setFirstResult(startRow);
            q.setMaxResults(count);

            final List<Object> list = q.list();

            final List<ObjectFile> objFileList = new ArrayList<>();

            for (final Object o : list) {
                final ObjectFile objectFile = findByOid(o.getOid(), s);
                if (objectFile == null) {
                    logger.trace("No object file for oid={}", o.getOid());
                    continue;
                }
                objFileList.add(findByOid(o.getOid()));
            }
            return objFileList;
        } catch (HibernateException e) {
            logger.error("Error finding paged result", e);
            return Collections.emptyList();
        } finally {
            s.close();
        }
    }

    //Keeps the session open
    private ObjectFile findByOid(final int oid, final Session s) {
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ObjectFile where oid = :param");
            q.setParameter("param", oid);
            final List<ObjectFile> list = q.list();

            if (list.isEmpty()) {
                logger.trace("Empty list for oid={}", oid);
            }
            return list.isEmpty() ? null : list.get(0);
        } catch (HibernateException e) {
            logger.error("Error finding by oid={}", oid, e);
            throw e;
        }
    }

}

