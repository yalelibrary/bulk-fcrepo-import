package edu.yale.library.ladybird.tests;

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
 * Tests html for user crud related page(s)
 */
public class ObjectViewIT extends AbstractWarTest {
    private static final Logger logger = LoggerFactory.getLogger(ObjectViewIT.class);

    /**
     * Page to test
     */
    private static final String PAGE_TO_TEST = getUrl("object/Object");

    @BeforeClass
    public static void setup() throws MalformedURLException {
        logger.debug("Setting up container");
        setupContainer();
    }

    @AfterClass
    public static void tearDown() {
        //TODO
    }

    @Test
    public void shouldContainEmptyTable() throws IOException {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        final HtmlPage page = webClient.getPage(PAGE_TO_TEST);
        final String pageAsText = page.asText();
        Assert.assertTrue(pageAsText.contains("No records found."));
        webClient.closeAllWindows();
    }

    @Test
    public void shouldContainColumn() throws Exception {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        final HtmlPage page = webClient.getPage(PAGE_TO_TEST);
        final String pageAsText = page.asText();
        Assert.assertTrue(pageAsText.contains("ProjectId"));
        webClient.closeAllWindows();
    }
}
