package edu.yale.library.ladybird.persistence.dao;



import edu.yale.library.ladybird.persistence.ServicesManager;

import java.sql.SQLException;

/**
 *
 */
public class AbstractPersistenceTest {

    private static final ServicesManager servicesManager = new ServicesManager();
    private static boolean containerStarted = false;

    public static void initDB() {

        if (containerStarted) {
            return;
        }
        try {
            servicesManager.initDB();
            containerStarted = true;
        } catch (Exception e) {
            e.printStackTrace();  //TODO
        }

    }

    //TODO
    public static void stopDB() throws SQLException {
        if (!containerStarted) {
            return;
        }
        try {
            servicesManager.stopDB();
        } catch (SQLException e) {
            e.printStackTrace();  //TODO
        }
    }

}
