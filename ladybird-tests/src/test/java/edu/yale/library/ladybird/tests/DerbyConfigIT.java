package edu.yale.library.ladybird.tests;

import edu.yale.library.ladybird.kernel.AppConfigException;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.persistence.ServicesManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    private ServicesManager servicesManager;

    private static final String TESTDB = "pamoja";

    @Before
    public void init() {
        servicesManager = new ServicesManager();
    }


    //TODO might conflict with testing shutdown
    @After
    public void shtudown() {
        try {
            servicesManager.stopDB();
        } catch (SQLException e) {
            //TODO error code check
            e.printStackTrace();
        }
    }

    @Test
    public void testMultipleInst() {
        servicesManager.startDB(); //TODO chk w. @Before
        try {
            servicesManager.startDB();
            fail("Failed. Tried to re-init driver.");
        } catch (AppConfigException e) {
            if (!e.getMessage().equalsIgnoreCase(ApplicationProperties.ALREADY_RUNNING)) {
                fail("Failed. Tried to re-init driver.");
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testTableInit() throws SQLException {
        servicesManager.initDB();
        loadDataJdbc();
        fetchData();
    }

    public void loadDataJdbc() {
        Connection conn;
        //List<PreparedStatement> statements = new ArrayList<>();
        PreparedStatement stmt;
        final String protocol = "jdbc:derby:";
        final String dbName = "memory:" + TESTDB;
        try {
            Properties props = new Properties();
            props.put("user", TESTDB);
            conn = DriverManager.getConnection(protocol + dbName + ";create=false", props);
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(
                    "insert into users values (?,?,?,?,?,?,?,?,?,?)");
            //statements.add(stmt);
            stmt.setInt(1, 1956);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, "test_user");
            stmt.setString(4, "test_pw");
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(8, 555);
            stmt.setString(9, "John Doe");
            stmt.setString(10, "doe@doe.pk");
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException s) {
            s.printStackTrace();
            //TODO error code check
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception loading data");
        }
    }

    public void fetchData() throws SQLException {
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
                String userName = rs.getString("username");
                String userPwd = rs.getString("password");
                assertEquals("Username mismatch.", userName, "test_user");
                assertEquals("Password mismatch.", userPwd, "test_pw");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Exception getting data");
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
