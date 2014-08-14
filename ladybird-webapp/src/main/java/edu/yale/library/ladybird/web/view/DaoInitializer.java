package edu.yale.library.ladybird.web.view;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import static org.slf4j.LoggerFactory.getLogger;

/**
 */

@ManagedBean
@ApplicationScoped
@SuppressWarnings("unchecked")
public class DaoInitializer {
    private static final Logger logger = getLogger(DaoInitializer.class);

    private static final Injector injector;

    static {
        injector = Guice.createInjector(new DaoHibernateModule());
    }

    public static void injectFields(Object object) {
        injector.injectMembers(object);
    }
}
