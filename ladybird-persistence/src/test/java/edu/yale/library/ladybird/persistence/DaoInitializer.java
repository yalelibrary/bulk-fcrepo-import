package edu.yale.library.ladybird.persistence;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;


@SuppressWarnings("unchecked")
public class DaoInitializer {

    private static final Injector injector;

    static {
        injector = Guice.createInjector(new DaoHibernateModule());
    }

    public static void injectFields(Object object) {
        injector.injectMembers(object);
    }
}
