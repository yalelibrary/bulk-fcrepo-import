package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.persistence.ServicesManager;

import java.sql.SQLException;

/**
 *
 */
public class AbstractDBTest {

    private ServicesManager servicesManager;
    static boolean dbInit = false;

    protected void init() {
        servicesManager = new ServicesManager();

        if (dbInit == false) {
            servicesManager.initDB();
            dbInit = true;
        }
    }

    protected void stop() throws SQLException {
        if (dbInit == true) {
            servicesManager.stopDB();
            dbInit = false;
        }
    }


}
