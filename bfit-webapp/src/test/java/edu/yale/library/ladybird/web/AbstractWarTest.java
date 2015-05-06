package edu.yale.library.ladybird.web;

import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.installer.Installer;
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;

public class AbstractWarTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWarTest.class);

    private static boolean CONTAINER_IS_RUNNING = false;
    private static final String ARTIFACT_ID = getProp("app.prefix");
    private static final String ARTIFACT_ID_VERSION = getProp("context.path");
    private static final String TEST_PORT = getProp("test.port");
    private static final String SERVER_PREFIX = "http://localhost:" + TEST_PORT;
    private static final String TOMCAT_7_X_ID = "tomcat7x";
    private static final String TOMCAT_INSTALL_URL = "http://archive.apache.org/dist/tomcat/tomcat-7"
                                                         + "/v7.0.42/bin/apache-tomcat-7.0.42-windows-x86.zip";
    private static final String TMP_DIR = getProp("java.io.tmpdir");
    private static final String WAR = TMP_DIR + ARTIFACT_ID + ARTIFACT_ID_VERSION + ".war";

    public static void setupContainer()  {
        if (CONTAINER_IS_RUNNING) {
            return;
        }

        try {
            final Installer installer = new ZipURLInstaller(new URL(TOMCAT_INSTALL_URL));
            logger.debug("Installing test container...");
            installer.install();
            logger.debug("Installed test container");

            final LocalConfiguration configuration = (LocalConfiguration) new DefaultConfigurationFactory()
                    .createConfiguration(TOMCAT_7_X_ID, ContainerType.INSTALLED, ConfigurationType.STANDALONE);
            configuration.setProperty(ServletPropertySet.PORT, TEST_PORT);
            final InstalledLocalContainer container =
                    (InstalledLocalContainer) new DefaultContainerFactory().createContainer(
                            TOMCAT_7_X_ID, ContainerType.INSTALLED, configuration);
            container.setHome(installer.getHome());
            logger.debug("Installed test container to={}", container.getHome());

            final Deployable war = new org.codehaus.cargo.container.deployable.WAR(WAR);
            configuration.addDeployable(war);
            logger.debug("Added test deployable={}", war.getFile());

            logger.debug("Starting test container...");
            container.start();
            CONTAINER_IS_RUNNING = true;
            logger.debug("Started test container");
        } catch (Exception e) {
            logger.error("Error", e);
            throw new RuntimeException("Error with test container", e);
        }
    }

    protected static String getAppUrl() {
        return SERVER_PREFIX + ARTIFACT_ID_VERSION;
    }

    private static String getProp(String s) {
        return System.getProperty(s);
    }

}
