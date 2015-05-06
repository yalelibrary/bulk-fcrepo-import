package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.entity.ImportJobNotifications;
import edu.yale.library.ladybird.persistence.dao.ImportJobNotificationsDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportJobNotificationsHibernateDAO extends GenericHibernateDAO<ImportJobNotifications, Integer> implements ImportJobNotificationsDAO {

    private Logger logger = getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @Override
    public List<ImportJobNotifications> findByUserAndJobId(int userId, int jobId) {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ImportJobNotifications "
                    + "where user_id = :param1 and import_job_id = :param2");
            q.setParameter("param1", userId);
            q.setParameter("param2", jobId);
            return (List<ImportJobNotifications>) q.list();
        } catch (Exception e) {
            logger.error("Error finding entity(s)", e);
            return null;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public List<ImportJobNotifications> findAllUnsent() {
        final Session s = getSession();

        try {
            final Query q = s.createQuery("from edu.yale.library.ladybird.entity.ImportJobNotifications "
                    + "where notified = 0");
            return (List<ImportJobNotifications>) q.list();
        } catch (Exception e) {
            logger.error("Error finding entity(s)", e);
            return null;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

