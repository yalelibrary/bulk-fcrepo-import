package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ProjectTemplateStrings;
import edu.yale.library.ladybird.persistence.dao.ProjectTemplateStringsDAO;
import org.hibernate.Query;

import java.util.List;

public class ProjectTemplateStringsHibernateDAO extends GenericHibernateDAO<ProjectTemplateStrings, Integer>
        implements ProjectTemplateStringsDAO {

    @Override
    public ProjectTemplateStrings findByFdidAndTemplateId(int fdid, int templateId) {
        try {
            final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ProjectTemplateStrings "
                    + "where fdid = :param1 and templateId = :param2");
            q.setParameter("param1", fdid);
            q.setParameter("param2", templateId);
            return (ProjectTemplateStrings) q.list().get(0);
        } catch (Exception e) {
            e.printStackTrace();  //TODO
            return null;
        }
    }

    @Override
    public List<ProjectTemplateStrings> findByTemplateId(int templateId) {
        try {
            final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ProjectTemplateStrings "
                    + "where templateId = :param");

            q.setParameter("param", templateId);
            return (List<ProjectTemplateStrings>) q.list();
        } catch (Exception e) {
            e.printStackTrace();  //TODO
            return null;
        }
    }

}

