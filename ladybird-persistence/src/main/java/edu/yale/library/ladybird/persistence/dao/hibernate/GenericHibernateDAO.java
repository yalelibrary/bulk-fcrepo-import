package edu.yale.library.ladybird.persistence.dao.hibernate;

import edu.yale.library.ladybird.persistence.HibernateUtil;
import edu.yale.library.ladybird.persistence.dao.GenericDAO;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
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

    protected StatelessSession getStatelessSession() {
        return HibernateUtil.getSessionFactory().openStatelessSession();
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public int count() {
        final Session s = getSession();
        final Query q = s.createQuery("select count(*) from " + persistentClass.getName());
        try {
            return ((Long) q.uniqueResult()).intValue();
        } finally {
            try {
                s.close();
            } catch (HibernateException e) {
                logger.trace("Error", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> find(int startRow, int count) {
        final Session s = getSession();
        Query q = s.createQuery("from " + persistentClass.getName());
        List l = q.list();
        try {
            s.close();
        } catch (HibernateException e) {
            logger.error("Error closing session", e);
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        final Session s = getSession();
        final Query q = s.createQuery("from " + persistentClass.getName());

        try {
            List l = q.list();
            return l;
        } catch (HibernateException e) {
            logger.error("Error finding all", e);
            return Collections.emptyList();
        } finally {
            try {
                s.close();
            } catch (HibernateException e) {
                logger.error("Error closing hibernate session");
            }

        }
    }

    public Integer save(T item) {
        logger.trace("Saving item={}", item.toString());
        Integer id = -1;
        Session s = null;
        Transaction tx = null;
        try {
            s = getSession();
            tx = s.beginTransaction();
            id = (Integer) s.save(item);
            s.flush();
            tx.commit();
            logger.trace("Saved item={}", item.toString());
        } catch (HibernateException t) {
            logger.error("Exception tyring to persist item.", t);
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Throwable rt) {
                logger.error("Exception rolling back transaction", rt);
                throw rt;
            }
            throw t;
        } catch (Throwable t)  {
            logger.error("Generic error saving item", t);
        } finally {
            if (s != null) {
                s.close();
            }
        }
        return id;
    }

    /**
     * Save or update list
     *
     * @param item entity to save or update
     */
    public void saveOrUpdateItem(T item) {
        Integer id = -1;
        Session s = null;
        Transaction tx = null;
        try {
            s = getSession();
            tx = s.beginTransaction();

            s.saveOrUpdate(item);
            s.flush();
            tx.commit();
        } catch (HibernateException t) {
            logger.error("Exception tyring to persist item." + t.getMessage());
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Throwable rt) {
                logger.error("Exception rolling back transaction", rt);
                throw rt;
            }
            throw t;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Save or update item
     *
     * @param item entity to save or update
     */
    public void updateItem(T item) {
        Integer id = -1;
        Session s = null;
        Transaction tx = null;
        try {
            s = getSession();
            tx = s.beginTransaction();

            s.update(item);
            s.flush();
            tx.commit();
        } catch (HibernateException t) {
            logger.error("Exception tyring to persist item." + t.getMessage());
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Throwable rt) {
                logger.error("Exception rolling back transaction", rt);
                throw rt;
            }
            throw t;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Save or update list
     *
     * @param itemList list of entities
     */
    public void saveOrUpdateList(List<T> itemList) {
        Integer id = -1;
        Session s = null;
        Transaction tx = null;
        try {
            s = getSession();
            tx = s.beginTransaction();

            for (T item : itemList) {
                s.saveOrUpdate(item);
                s.flush();
            }
            tx.commit();
        } catch (HibernateException t) {
            logger.error("Exception tyring to persist item." + t.getMessage());
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Throwable rt) {
                logger.error("Exception rolling back transaction", rt);
                throw rt;
            }
            throw t;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Save list
     *
     * @param itemList list of entities
     */
    public void saveList(List<T> itemList) {
        logger.debug("Saving list of size={}", itemList.size());
        StatelessSession s = null;
        Transaction tx = null;
        try {
            s = getStatelessSession();
            tx = s.beginTransaction();

            for (int i = 0; i < itemList.size(); i++) {
                s.insert(itemList.get(i));
            }

            s.getTransaction().commit();
        } catch (HibernateException t) {
            logger.error("Exception tyring to persist item." + t.getMessage());
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Throwable rt) {
                logger.error("Exception rolling back transaction", rt);
                throw rt;
            }
            throw t;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    /**
     * Delete list
     *
     * @param entities list of entites
     */
    @Override
    public void delete(List<T> entities) {
        Session s = null;
        Transaction tx = null;
        try {
            s = getSession();
            tx = s.beginTransaction();
            for (T item : entities) {
                s.delete(item);
            }
            s.flush();
            tx.commit();
        } catch (HibernateException t) {
            logger.error("Exception tyring to persist item." + t.getMessage());
            try {
                if (tx != null) {
                    tx.rollback();
                }
            } catch (Throwable rt) {
                logger.error("Exception rolling back transaction", rt);
                throw rt;
            }
            throw t;
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    @Override
    public void deleteAll() {
        final Session s = getSession();
        logger.trace("Deleting all");
        try {
            Transaction tx;
            tx = s.beginTransaction();
            final Query q = s.createQuery("delete from " + persistentClass.getName());
            int num = q.executeUpdate();
            s.flush();
            tx.commit();
            logger.debug("Deleted entries num={}", num);
        } catch (HibernateException f){
            logger.error("Error deleting all", f);
            throw f;
        }
        finally {
            if (s != null) {
                s.close();
            } else {
                logger.error("Session null");
            }
        }
    }
}
