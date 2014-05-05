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

public class AbstractWarTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWarTest.class);

    private static boolean containerStarted = false;

    private static final String ARTIFACTID = getProp("app.prefix");
    private static final String ARTIFACTID_VERSION = getProp("context.path");
    private static final String PORT = getProp("test.port");
    private static final String SERVER_PREFIX = "http://localhost:" + PORT;
    private static final String TOMCAT_7_X = "tomcat7x";
    private static final String TOMCAT_INSTALL_URL = "http://archive.apache.org/dist/tomcat/tomcat-7"
            + "/v7.0.42/bin/apache-tomcat-7.0.42-windows-x86.zip";
    private static final String DIR = getProp("java.io.tmpdir");
    private static final String WAR = DIR + ARTIFACTID + ARTIFACTID_VERSION + ".war";

    public static void setupContainer()  {

        if (containerStarted) {
            return;
        }

        final Deployable war = new org.codehaus.cargo.container.deployable.WAR(WAR);

        logger.debug("Installing test container");

        final Installer installer;
        try {
            installer = new ZipURLInstaller(new java.net.URL(TOMCAT_INSTALL_URL));
            installer.install();

            final LocalConfiguration configuration = (LocalConfiguration) new DefaultConfigurationFactory()
                    .createConfiguration(TOMCAT_7_X, ContainerType.INSTALLED, ConfigurationType.STANDALONE);
            configuration.setProperty(ServletPropertySet.PORT, PORT);
            final InstalledLocalContainer container =
                    (InstalledLocalContainer) new DefaultContainerFactory().createContainer(
                            TOMCAT_7_X, ContainerType.INSTALLED, configuration);
            container.setHome(installer.getHome());
            logger.debug("Installed test container to={}", container.getHome());

            configuration.addDeployable(war);
            logger.debug("Added deployable={}", war.getFile());

            container.start();
            logger.debug("Started container");

            containerStarted = true;
            return;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new RuntimeException("Error starting test container");
    }

    protected static String getAppUrl() {
        return SERVER_PREFIX + ARTIFACTID_VERSION;
    }

    private static String getProp(String s) {
        return System.getProperty(s);
    }

}
