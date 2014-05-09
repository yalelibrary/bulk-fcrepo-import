package edu.yale.library.ladybird.persistence.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;


@SuppressWarnings("unchecked")
public class TestDaoInitializer {

    private static final Injector injector;

    static {
        injector = Guice.createInjector(new TestDaoHibernateModule());
    }

    public static void injectFields(Object object) {
        injector.injectMembers(object);
    }
}
