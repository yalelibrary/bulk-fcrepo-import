package edu.yale.library.ladybird;

import edu.yale.library.ladybird.engine.ExportBus;
import edu.yale.library.ladybird.engine.model.FieldConstant;
import edu.yale.library.ladybird.engine.model.FieldDefinitionValue;
import edu.yale.library.ladybird.kernel.KernelBootstrap;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AppContextListener implements ServletContextListener {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AppContextListener.class);

    //TODO remove. Contains test fdids corresponding to test excel file (instead of via db)
    private static final String FDID_TEST_PROPS_FILE = "/fdids.test.properties";

    private final KernelBootstrap kernelBootstrap = new KernelBootstrap();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        kernelBootstrap.init();
        ExportBus exportBus = new ExportBus(); //TODO
        exportBus.init();
        try {
            setTextFieldDefsMap();
        } catch (IOException e) {
            e.printStackTrace();  //TODO
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        kernelBootstrap.stop();
    }

    /**
     * TODO remove
     * Helps in initing fdids via a text file
     * @throws IOException
     */
    @Deprecated
    public void setTextFieldDefsMap() throws IOException {
        Map<String, FieldConstant> fdidsMap = new HashMap<>();

        final Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream(FDID_TEST_PROPS_FILE));

        for (final String fdidInt : properties.stringPropertyNames()) {
            fdidsMap.put(properties.getProperty(fdidInt), getFdid(Integer.parseInt(fdidInt),
                    properties.getProperty(fdidInt)));
        }
        final FieldDefinitionValue fieldDefinitionValue = new FieldDefinitionValue();
        fieldDefinitionValue.setFieldDefMap(fdidsMap);
    }

    //TODO remove
    private FieldDefinitionValue getFdid(final int fdid, final String s) {
        return new FieldDefinitionValue(fdid, s);
    }

}
