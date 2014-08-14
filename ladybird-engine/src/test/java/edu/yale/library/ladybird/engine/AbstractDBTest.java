package edu.yale.library.ladybird.engine;


import edu.yale.library.ladybird.kernel.ServicesManager;

import java.sql.SQLException;

public class AbstractDBTest {

    private ServicesManager servicesManager;
    static boolean dbInit = false;

    protected void init() {
        servicesManager = new ServicesManager();

        if (!dbInit) {
            servicesManager.initDB();
            dbInit = true;
        }
    }

    protected void stop() throws SQLException {
        if (dbInit) {
            servicesManager.stopDB();
            dbInit = false;
        }
    }




}
