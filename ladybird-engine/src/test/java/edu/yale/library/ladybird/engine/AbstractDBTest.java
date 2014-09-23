package edu.yale.library.ladybird.engine;


import edu.yale.library.ladybird.kernel.ServicesManager;

import java.sql.SQLException;

//TODO clean up..redundant methods
public class AbstractDBTest {

    private ServicesManager servicesManager;

    private static ServicesManager servicesManager2;

    static boolean dbInit = false;

    protected synchronized void init() {
        servicesManager = new ServicesManager();

        if (!dbInit) {
            servicesManager.initDB();
            dbInit = true;
        }
    }

    //TODO
    protected synchronized void stop() throws SQLException {
        if (dbInit) {
            //servicesManager.stopDB();
            //dbInit = false;
        }
    }

    public static  synchronized void initDB() {
        servicesManager2 = new ServicesManager();

        if (dbInit) {
            System.out.println("Already init");
        }

        if (!dbInit) {
            servicesManager2.initDB();
            dbInit = true;
        }
    }

    public static synchronized void stopDB() throws SQLException {
        if (dbInit) {
            servicesManager2.stopDB();
            dbInit = false;
        }
    }




}
