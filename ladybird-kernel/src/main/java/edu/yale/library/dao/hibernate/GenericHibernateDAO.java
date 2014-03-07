package edu.yale.library.dao.hibernate;

import edu.yale.library.dao.GenericDAO;
import edu.yale.library.persistence.HibernateUtil;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.slf4j.Logger;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * @param <T>
 * @param <ID>
 */
public abstract class GenericHibernateDAO<T, ID extends Serializable>
        implements GenericDAO<T, ID> {

    private final Logger logger = getLogger(this.getClass());

    private Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public GenericHibernateDAO() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Session getSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    @SuppressWarnings("unchecked")
    public List<T> find(int startRow, int count) {
        logger.debug("Finding subset, startRow={}, count={}", startRow, count);
        Query q = getSession().createQuery("from " + persistentClass.getName());
        return q.list();
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        Query q = getSession().createQuery("from " + persistentClass.getName());
        return q.list();
    }

    public Integer save(T item) {
        Integer id = -1;
        Session s = null;
        Transaction tx = null;
        try {
            //logger.debug("Getting session");
            s = getSession();
            tx = s.beginTransaction();
            //logger.debug("Saving item: " + item.toString());
            id = (Integer) s.save(item);
            s.flush();
            tx.commit();
            //logger.debug("Saved item");
        } catch (HibernateException t) {
            logger.error("Exception tyring to persist item." + t.getMessage());
            t.printStackTrace();
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Throwable rt) {
                logger.error("Exception rolling back transaction", rt);
                rt.printStackTrace();
                throw rt;
            }
            throw t;
        } finally {
            if (s != null) {
                s.close();
            }
        }
        return id;
    }


}
