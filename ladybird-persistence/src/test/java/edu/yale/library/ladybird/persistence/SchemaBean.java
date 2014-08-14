package edu.yale.library.ladybird.persistence;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SchemaBean {
    final PropertyReader reader = new PropertyReader(ApplicationProperties.SCHEMA_PROPS_FILE);
    final PropertyReader killReader = new PropertyReader(ApplicationProperties.KILL_SCHEMA_PROPS_FILE);

    private Map<String,String> build(PropertyReader reader) throws IOException {
        final Map<String, String> map = new HashMap<>();
        final Properties props = reader.readAll();
        for (final String key : props.stringPropertyNames()) {
            map.put(key, value(props.getProperty(key)));
        }
        return map;
    }

    public Map<String,String> getSchema() {
        try {
            final Map<String,String> map = new SchemaBean().build(reader);
            return Collections.unmodifiableMap(map);
        } catch (IOException e) {
            return Collections.emptyMap(); //ignore exception
        }
    }

    public Map<String,String> getKillSchema() {
        try {
            final Map<String,String> map = new SchemaBean().build(killReader);
            return Collections.unmodifiableMap(map);
        } catch (IOException e) {
            return Collections.emptyMap(); //ignore exception
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
     * A filter transforms the schema in some way.
     */
    interface SchemaFilter {
        String filter(final String s);
    }

    /**
     * TODO re-visit this as schema is changed.
     * <p/>
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
