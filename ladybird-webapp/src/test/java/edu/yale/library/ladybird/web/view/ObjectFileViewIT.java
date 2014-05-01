package edu.yale.library.ladybird.web.view;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.yale.library.ladybird.web.AbstractWarTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import java.net.MalformedURLException;

/**
 * Tests html for user crud related page(s)
 */
public class ObjectFileViewIT extends AbstractWarTest {
    private final Logger logger = getLogger(this.getClass());

    /**
     * Page to test
     */
    private static final String PAGE_TO_TEST = getUrl("object/object_file");

    @BeforeClass
    public static void setup() throws MalformedURLException {
        setupContainer();
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
        Assert.assertTrue(pageAsText.contains("FileName"));
        Assert.assertTrue(pageAsText.contains("No records found."));
        webClient.closeAllWindows();
    }
}


