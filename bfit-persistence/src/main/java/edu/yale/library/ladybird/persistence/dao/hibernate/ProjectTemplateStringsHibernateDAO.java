package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateStringsDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ProjectTemplateStringsHibernateDAO extends GenericHibernateDAO<ProjectTemplateStrings, Integer>
        implements ProjectTemplateStringsDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectTemplateStringsHibernateDAO.class);

    @SuppressWarnings("unchecked")
    @Override
    public ProjectTemplateStrings findByFdidAndTemplateId(int fdid, int templateId) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ProjectTemplateStrings "
                    + "where fdid = :param1 and templateId = :param2");
            q.setParameter("param1", fdid);
            q.setParameter("param2", templateId);
            //return (ProjectTemplateStrings) q.list().get(0);
            List<ProjectTemplateStrings> list = q.list();
            return (list.isEmpty()) ? null : list.get(0);
        } catch (Exception e) {
            logger.error("Error getting by fdid and template id", e);
            throw e;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<ProjectTemplateStrings> findByTemplateId(int templateId) {
        final Session s = getSession();
        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ProjectTemplateStrings "
                    + "where templateId = :param");

            q.setParameter("param", templateId);
            return (List<ProjectTemplateStrings>) q.list();
        } catch (Exception e) {
            logger.error("Error finding template id", e);
            throw e;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}

