package ladybird;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Tests html for user related pages
 */
public class UserViewIT extends AbstractWarTest {
    private static final Logger logger = LoggerFactory.getLogger(UserViewIT.class);

    /**
     * Page to test
     */
    private static final String PAGE = "http://localhost:8081/ladybird-webapp-0.0.1-SNAPSHOT/pages/Users.xhtml";

    @BeforeClass
    public static void setup() throws MalformedURLException {
        logger.debug("Setting up container");
        setupContainer();
    }

    @AfterClass
    public static void tearDown() {
        logger.debug("Tearing down container");
    }

    @Test
    public void shouldContainEmptyTable() throws IOException {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        final HtmlPage page = webClient.getPage(PAGE);
        final String pageAsText = page.asText();
        Assert.assertTrue(pageAsText.contains("No records found."));
        webClient.closeAllWindows();
    }

    @Test
    public void shouldContainString_LastUsed() throws Exception {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        final HtmlPage page = webClient.getPage(PAGE);
        final String pageAsText = page.asText();
        Assert.assertTrue(pageAsText.contains("Last used"));
        webClient.closeAllWindows();
    }
}
