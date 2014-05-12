package edu.yale.library.ladybird.web.view;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.yale.library.ladybird.web.AbstractWarTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.net.MalformedURLException;

import static org.junit.Assert.fail;

public class ObjectFileViewIT extends AbstractWarTest {

    /**
     * Page to test
     */
    private static final String PAGE_TO_TEST = getAppUrl() + "/pages/secure/object/object_file.xhtml";

    @BeforeClass
    public static void setup() throws MalformedURLException {
        try {
            AbstractWarTest.setupContainer();
        } catch (RuntimeException e) {
            fail("Error starting container");
        }
    }

    @AfterClass
    public static void tearDown() {
        //TODO
    }

    @Test
    public void shouldContainColumn() throws Exception {
        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        final HtmlPage page = webClient.getPage(PAGE_TO_TEST);
        final String pageAsText = page.asText();
        Assert.assertTrue(pageAsText.contains("File"));
        Assert.assertTrue(pageAsText.contains("No records found."));
        webClient.closeAllWindows();
    }
}


