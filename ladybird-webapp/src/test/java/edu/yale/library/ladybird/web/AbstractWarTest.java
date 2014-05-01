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

import java.net.MalformedURLException;

public class AbstractWarTest {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWarTest.class);

    private static final String TOMCAT_INSTALL_URL = "http://archive.apache.org/dist/tomcat/tomcat-7"
            + "/v7.0.42/bin/apache-tomcat-7.0.42-windows-x86.zip";
    private static final String APP_ARTIFACT = "ladybird-webapp-0.0.1-SNAPSHOT";
    private static final String APP_PREFIX = "/ladybird-webapp/";
    private static final String DEFAULT_PAGE_PREFIX = "/pages/secure/";
    private static final String TEST_PORT = "8081";
    private static final String SERVER_PREFIX = "http://localhost:" + TEST_PORT + "/";
    private static final String TOMCAT_7_X = "tomcat7x";
    private static final String WAR = System.getProperty("java.io.tmpdir") + APP_PREFIX + APP_ARTIFACT + ".war";
    private static boolean containerStarted = false;

    protected static void setupContainer() throws MalformedURLException {

        if (containerStarted) {
            return; //already running
        }

        final Deployable war = new org.codehaus.cargo.container.deployable.WAR(WAR);

        logger.debug("Installing test container");

        final Installer installer = new ZipURLInstaller(new java.net.URL(TOMCAT_INSTALL_URL));
        installer.install();

        final LocalConfiguration configuration = (LocalConfiguration) new DefaultConfigurationFactory()
                .createConfiguration(TOMCAT_7_X, ContainerType.INSTALLED, ConfigurationType.STANDALONE);
        configuration.setProperty(ServletPropertySet.PORT, TEST_PORT);
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
    }

    protected static String getUrl(final String s) {
        return  SERVER_PREFIX + APP_ARTIFACT + DEFAULT_PAGE_PREFIX + s + ".xhtml";
    }
}
