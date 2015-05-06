package edu.yale.library.ladybird.kernel.db;


import edu.yale.library.ladybird.kernel.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public final class DerbySchemaUtil {

    private static final Logger logger = LoggerFactory.getLogger(DerbySchemaUtil.class);

    /**
     * Init schema
     *
     * @throws EmbeddedDBException
     */
    protected void init() {
        try {
            final Connection conn = DriverManager.getConnection(Config.PROTOCOL
                    + Config.DB + ";create=true", Config.PROPS);
            logger.debug("Connected to DB and created a schema: " + Config.DB);
            conn.setAutoCommit(false);
            final Statement statement = conn.createStatement();

            logger.trace("Creating table(s)");

            final SchemaUtil schemaUtil = new SchemaUtil();
            final Map<String, String> m = schemaUtil.getSchema();

            if (m == null || 0 == m.size()) {
                throw new EmbeddedDBException("Schema empty");
            }
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry p = (Map.Entry) it.next();
                logger.trace("Executing create statement for table={}", p.getKey());
                statement.execute(p.getValue().toString());
            }
            conn.commit();
            logger.debug("Created table(s)");


            final Map<String, String> fdidSchema = schemaUtil.getFdidSchema();

            if (fdidSchema == null || 0 == m.size()) {
                throw new EmbeddedDBException("Schema empty");
            }
            Iterator it1 = fdidSchema.entrySet().iterator();

            while (it1.hasNext()) {
                final Map.Entry p = (Map.Entry) it1.next();
                logger.trace("Executing create statement for table={}", p.getKey());
                statement.execute(p.getValue().toString());
            }
            conn.commit();
            logger.debug("Init fdids(s)");
        } catch (SQLException e) {
            throw new EmbeddedDBException(e);
        }
    }

    /**
     * Remove schema
     *
     * @throws EmbeddedDBException
     */
    protected void killSchema() {
        try {
            final Connection conn = DriverManager.getConnection(Config.PROTOCOL
                    + Config.DB + ";create=false", Config.PROPS);
            conn.setAutoCommit(false);
            final Statement statement = conn.createStatement();

            logger.debug("Killing table(s)");

            final SchemaUtil schemaUtil = new SchemaUtil();
            final Map<String, String> m = schemaUtil.getKillSchema();

            if (m == null || 0 == m.size()) {
                throw new EmbeddedDBException("Schema empty");
            }
            Iterator it = m.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry p = (Map.Entry) it.next();
                logger.trace("Executing drop statement for table={}",  p.getKey());
                statement.execute(p.getValue().toString());
            }
            conn.commit();
        } catch (SQLException e) {
            throw new EmbeddedDBException(e);
        }
    }

    /**
     * A filter transforms the schema in some way.
     */
    interface SchemaFilter {
        String filter(final String s);
    }


    /**
     * General config settings
     */
    private static final class Config {
        static final String PROTOCOL = "jdbc:derby:";
        static final String DB = "memory:pamoja";
        static final String DB_USER = "pamoja";
        static final Properties PROPS = new Properties();

        static {
            PROPS.put("user", Config.DB_USER);
        }

        private Config() {
        }
    }

    public class SchemaUtil {

        private final Logger logger = LoggerFactory.getLogger(SchemaUtil.class);

        private final PropertyReader reader = new PropertyReader(ApplicationProperties.SCHEMA_PROPS_FILE);

        private final PropertyReader fdidReader = new PropertyReader(ApplicationProperties.FDID_PROPS_FILE);

        private final PropertyReader killReader = new PropertyReader(ApplicationProperties.KILL_SCHEMA_PROPS_FILE);

        private Map<String, String> build(PropertyReader reader) throws IOException {
            final Map<String, String> map = new HashMap<>();
            final Properties props = reader.readAll();
            for (final String key : props.stringPropertyNames()) {
                map.put(key, value(props.getProperty(key)));
            }
            return map;
        }

        public Map<String, String> getSchema() {
            try {
                final Map<String, String> map = new SchemaUtil().build(reader);
                return Collections.unmodifiableMap(map);
            } catch (IOException e) {
                return Collections.emptyMap();
            }
        }

        public Map<String, String> getFdidSchema() {
            try {
                final Map<String, String> map = new SchemaUtil().build(fdidReader);
                return Collections.unmodifiableMap(map);
            } catch (IOException e) {
                logger.error("Error reading properties for init fdid", e);
                return Collections.emptyMap();
            }
        }

        public Map<String, String> getKillSchema() {
            try {
                final Map<String, String> map = new SchemaUtil().build(killReader);
                return Collections.unmodifiableMap(map);
            } catch (IOException e) {
                logger.error("Error ridding of schema", e);
                return Collections.emptyMap();
            }
        }

        /**
         * Reads properties.
         */
        private class PropertyReader {
            final String path;

            PropertyReader(final String path) {
                this.path = path;
            }

            private Properties readAll() throws IOException {
                Properties properties = new Properties();
                properties.load(this.getClass().getResourceAsStream(path));
                return properties;
            }
        }

        /**
         * TODO re-visit this as schema is changed.
         * Filters value. Method subject to removal.
         *
         * @param statement sql statement
         * @return transformed value
         */
        private String value(final String statement) {
            final SchemaFilter f = (s) -> s.replace("`", "")
                    .replace("int(11)", "int")
                    .replace("datetime", "timestamp")
                    .replace("longtext", "blob");

            return f.filter(statement);
        }

    }



    }

