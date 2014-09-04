package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.UserProjectFieldExportOptions;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldExportOptionsDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

public class UserProjectFieldExportOptionsHibernateDAO extends GenericHibernateDAO<UserProjectFieldExportOptions, Integer> implements UserProjectFieldExportOptionsDAO {

    /**
     * Returns an entry (indicating presence) or null
     *
     * @param userId    user id
     * @param projectId project id
     * @param fdid      fdid
     * @return entry or null
     */
    @Override
    public UserProjectFieldExportOptions findByUserAndProjectAndFdid(int userId, int projectId, int fdid) {
        final Session s = getSession();

        try {
            Query q = s.createQuery("from edu.yale.library.ladybird.entity.UserProjectFieldExportOptions "
                    + "where userId= :param1 and projectId = :param2 and fdid = :param3");
            q.setParameter("param1", userId);
            q.setParameter("param2", projectId);
            q.setParameter("param3", fdid);
            List<UserProjectFieldExportOptions> list = q.list();
            return (q.list().isEmpty()) ? null : list.get(0);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

