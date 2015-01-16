package edu.yale.library.ladybird.engine;


import edu.yale.library.ladybird.kernel.db.EmbeddedDBServicesManager;

import java.sql.SQLException;

//TODO redundant methods
public class AbstractDBTest {

    private EmbeddedDBServicesManager embeddedDBServicesManager;

    private static EmbeddedDBServicesManager embeddedDBServicesManager2;

    static boolean dbInit = false;

    protected synchronized void init() {
        embeddedDBServicesManager = new EmbeddedDBServicesManager();

        if (!dbInit) {
            embeddedDBServicesManager.initDB();
            dbInit = true;
        }
    }

    protected synchronized void stop() throws SQLException {
        if (dbInit) {
            //servicesManager.stopDB();
            //dbInit = false;
        }
    }

    public static  synchronized void initDB() {
        embeddedDBServicesManager2 = new EmbeddedDBServicesManager();

        if (dbInit) {
            System.out.println("Already init");
        }

        if (!dbInit) {
            embeddedDBServicesManager2.initDB();
            dbInit = true;
        }
    }

    public static synchronized void stopDB() throws SQLException {
        if (dbInit) {
            embeddedDBServicesManager2.stopDB();
            dbInit = false;
        }
    }

}
