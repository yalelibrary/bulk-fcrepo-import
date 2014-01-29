package edu.yale.library;

import edu.yale.library.persistence.derby.SchemaBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map;


/**
 *
 */
public final class DerbySchemaUtil
{
    private static final Logger logger = LoggerFactory.getLogger(DerbySchemaUtil.class);

    /**
     * Init schema
     * @throws AppConfigException
     */
    protected void init() throws AppConfigException
    {
        try
        {
            Connection conn = DriverManager.getConnection(Config.PROTOCOL +
                    Config.DB + ";create=true", Config.PROPS);
            logger.debug("Connected to DB and created a schema: " + Config.DB);
            conn.setAutoCommit(false);
            Statement statement = conn.createStatement();
            logger.debug("Creating table(s)");

            final java.util.Map<String, String> m = SchemaBean.getSchema();
            if (m == null || 0 == m.size())
                throw new AppConfigException("Schema empty");
            Iterator it = m.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry p = (Map.Entry)it.next();
                logger.debug("Executing: " + p.getKey() + " = " + p.getValue());
                statement.execute(p.getValue().toString());
            }
            conn.commit();
            logger.debug("Created table(s)");
        }
        catch (SQLException e)
        {
            throw new AppConfigException(e);
        }
    }

    /**
     * General config settings
     */
    private static final class Config
    {
        final static String PROTOCOL = "jdbc:derby:";

        final static String DB = "memory:PAMOJADB";

        final static String DB_USER = "PAMOJADB";

        final static Properties PROPS = new Properties();

        static
        {
            PROPS.put("user", Config.DB_USER);
        }

        private Config() {}
    }


}

