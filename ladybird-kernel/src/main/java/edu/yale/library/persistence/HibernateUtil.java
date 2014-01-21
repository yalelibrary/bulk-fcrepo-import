package edu.yale.library.persistence;


import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

    final static Logger logger = LoggerFactory
            .getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static final String cfg_xml = "hibernate.cfg.xml";

    private static SessionFactory buildSessionFactory()
    {
        logger.debug("Building Hibernate Session Factory");
        try
        {
            Configuration configuration = new Configuration();
            configuration.configure(cfg_xml);
            ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            SessionFactory sessionFactory = configuration
                    .buildSessionFactory(serviceRegistryBuilder
                            .buildServiceRegistry());
            return sessionFactory;
        }
        catch (Throwable ex)
        {
            logger.error("Exception encountered while building session factory");
            throw new ExceptionInInitializerError(ex); // or ex.
        }
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public static void shutdown()
    {
        logger.debug("Shutting down Hibernate Session Factory");
        try
        {
            getSessionFactory().close();
        }
        catch (HibernateException e)
        {
            throw new HibernateException(e);
        }
    }
}