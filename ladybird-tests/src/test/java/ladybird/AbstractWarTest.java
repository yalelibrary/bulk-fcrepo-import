package ladybird;


import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.WAR;
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

    /**
     * Tomcat zip to download
     */
    protected static final String TOMCAT_INSTALL_URL = "http://archive.apache.org/dist/tomcat/tomcat-7"
            + "/v7.0.42/bin/apache-tomcat-7.0.42-windows-x86.zip";

    /**
     * Location of the war file to shouldContainEmptyTable
     */
    protected static final String WAR = System.getProperty("user.home") + "/ladybird-webapp/"
            + "ladybird-webapp-0.0.1-SNAPSHOT.war";
    private static final String TEST_PORT = "8081";
    private static final String TOMCAT_7_X = "tomcat7x";

    protected static void setupContainer() throws MalformedURLException {
        Deployable war = new org.codehaus.cargo.container.deployable.WAR(WAR);

        logger.debug("Installing test container");

        Installer installer = new ZipURLInstaller(
                new java.net.URL(TOMCAT_INSTALL_URL));
        installer.install();

        LocalConfiguration configuration = (LocalConfiguration) new DefaultConfigurationFactory().createConfiguration(
                "tomcat7x", ContainerType.INSTALLED, ConfigurationType.STANDALONE);
        configuration.setProperty(ServletPropertySet.PORT, TEST_PORT);
        InstalledLocalContainer container =
                (InstalledLocalContainer) new DefaultContainerFactory().createContainer(
                        TOMCAT_7_X, ContainerType.INSTALLED, configuration);
        container.setHome(installer.getHome());

        logger.debug("Installed test container to={}", container.getHome());

        configuration.addDeployable(war);
        logger.debug("Added deployable={}", war.getFile());

        container.start();
        logger.debug("Started container");
    }
}
