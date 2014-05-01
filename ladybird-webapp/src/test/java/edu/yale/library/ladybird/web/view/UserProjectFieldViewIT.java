package edu.yale.library.ladybird.web.view;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.yale.library.ladybird.web.AbstractWarTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Tests html for user crud related page(s)
 */
public class UserProjectFieldViewIT extends AbstractWarTest {
    private static final Logger logger = LoggerFactory.getLogger(UserProjectFieldViewIT.class);

    /**
     * Page to test
     */
    private static final String PAGE_TO_TEST = getUrl("user_project_field");

    @BeforeClass
    public static void setup() throws MalformedURLException {
        setupContainer();
    }

    @AfterClass
    public static void tearDown() {
        //TODO
    }

    @Test
    public void shouldContainString() throws Exception {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        final HtmlPage page = webClient.getPage(PAGE_TO_TEST);
        final String pageAsText = page.asText();
        Assert.assertTrue(pageAsText.contains("No records found."));
        Assert.assertTrue(pageAsText.contains("Role"));
        Assert.assertTrue(pageAsText.contains("Fdid"));
        webClient.closeAllWindows();
    }
}
