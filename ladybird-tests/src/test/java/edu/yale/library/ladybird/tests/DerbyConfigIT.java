package edu.yale.library.ladybird.tests;

import edu.yale.library.ladybird.kernel.AppConfigException;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.persistence.ServicesManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.sql.Statement;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DerbyConfigIT {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServicesManager servicesManager;
    private static final String TESTDB = "pamoja";


    @Before
    public void init() {
        servicesManager = new ServicesManager();
    }

    @After
    public void shtudown() {
        try {
            servicesManager.stopDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleInst() {
        servicesManager.startDB();
        try {
            servicesManager.startDB();
            fail("Failed. Tried to re-init driver.");
        } catch (AppConfigException e) {
            if (!e.getMessage().equalsIgnoreCase(ApplicationProperties.ALREADY_RUNNING)) {
                logger.error("Error", e);
                fail("Failed. Tried to re-init driver.");
            }
        }
    }

    @Test
    public void testTableInit() throws SQLException {
        servicesManager.initDB();
        loadDataJdbc();
        fetchData();
    }

    private void loadDataJdbc() {
        Connection conn;
        PreparedStatement stmt;
        final String protocol = "jdbc:derby:";
        final String dbName = "memory:" + TESTDB;
        try {
            Properties props = new Properties();
            props.put("user", TESTDB);
            conn = DriverManager.getConnection(protocol + dbName + ";create=false", props);
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("insert into users values (?,?,?,?,?,?,?,?,?,?)");

            final Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

            stmt.setInt(1, 1956);
            stmt.setTimestamp(2, timeStamp);
            stmt.setString(3, "test_user");
            stmt.setString(4, "test_pw");
            stmt.setTimestamp(5, timeStamp);
            stmt.setTimestamp(6, timeStamp);
            stmt.setTimestamp(7, timeStamp);
            stmt.setInt(8, 555);
            stmt.setString(9, "John Doe");
            stmt.setString(10, "doe@doe.pk");

            stmt.executeUpdate();

            conn.commit();
        } catch (SQLException s) {
            logger.error("Error", s);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error retrieving data from derby");
        }
    }

    private void fetchData() throws SQLException {
        Connection conn = null;
        Statement statement = null;
        final String protocol = "jdbc:derby:";
        final String dbName = "memory:" + TESTDB;
        final String selectTableSQL = "SELECT username, password from USERS";
        try {
            Properties props = new Properties();
            props.put("user", TESTDB);
            conn = DriverManager.getConnection(protocol + dbName + ";create=false", props);
            statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                final String userName = rs.getString("username");
                final String userPwd = rs.getString("password");

                assertEquals("Value mismatch.", userName, "test_user");
                assertEquals("Value mismatch.", userPwd, "test_pw");
            }
        } catch (SQLException e) {
            logger.error("Error", e);
            fail("Error retrieving data from derby");
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

}
