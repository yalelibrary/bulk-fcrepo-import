package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportJobNotifications;
import edu.yale.library.ladybird.persistence.dao.ImportJobNotificationsDAO;
import org.hibernate.Query;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ImportJobNotificationsHibernateDAO extends GenericHibernateDAO<ImportJobNotifications, Integer> implements ImportJobNotificationsDAO {

    private Logger logger = getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportJobNotifications> findByUserAndJobId(int userId, int jobId) {
        try {
            final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ImportJobNotifications "
                    + "where user_id = :param1 and import_job_id = :param2");
            q.setParameter("param1", userId);
            q.setParameter("param2", jobId);
            return (List<ImportJobNotifications>) q.list();
        } catch (Exception e) {
            logger.error("Error finding entity(s)", e);
            return null;
        }
    }

    @Override
    public List<ImportJobNotifications> findAllUnsent() {
        try {
            final Query q = getSession().createQuery("from edu.yale.library.ladybird.entity.ImportJobNotifications "
                    + "where notified = 0");
            return (List<ImportJobNotifications>) q.list();
        } catch (Exception e) {
            logger.error("Error finding entity(s)", e);
            return null;
        }
    }
}

