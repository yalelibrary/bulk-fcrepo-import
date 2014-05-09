package edu.yale.library.ladybird.web.view;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.yale.library.ladybird.web.AbstractWarTest;
import edu.yale.library.ladybird.web.http.HttpServiceTestUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserEventViewIT extends AbstractWarTest {

    /**
     * Page to test
     * TODO read param from POST
     */
    private static final String PAGE_TO_TEST = getAppUrl() + "/pages/secure/user_event.xhtml?id=1";

    @BeforeClass
    public static void setup() throws Exception {
        try {
            AbstractWarTest.setupContainer();
        } catch (RuntimeException e) {
            fail("Error starting container");
        }
    }

    @After
    public void cleanUp() throws Exception {
        final HttpResponse response = execDelete("users");
        assertEquals("Not modified", response.getStatusLine().getStatusCode(), 200);

        final HttpResponse response2 = execDelete("userevents");
        assertEquals("Not modified", response2.getStatusLine().getStatusCode(), 200);
    }

    @Test
    public void shouldPopulateDatatable() throws Exception {
        createHttpUser("users", getParamsForUserHttpService());

        final WebClient webClient = new WebClient();
        webClient.setJavaScriptEnabled(false);
        webClient.closeAllWindows();

        final HtmlPage page = webClient.getPage(PAGE_TO_TEST);
        final String pageAsText = page.asXml();
        assertTrue(pageAsText.contains("Event Type"));
        final HtmlElement htmlElement = page.getElementById("UserEventForm:UserEventDatatable:0:event_type");
        assertEquals("Value mismatch", htmlElement.getTextContent(), "user.create");
    }

    private void createHttpUser(final String service, final List<NameValuePair> urlParameters) throws Exception {
        final HttpServiceTestUtil httpServiceTestUtil = new HttpServiceTestUtil();
        final HttpPost post = httpServiceTestUtil.doPOST(service);

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        final HttpResponse response = httpServiceTestUtil.getHttpClient().execute(post);
        assert (response.getStatusLine().getStatusCode() == 200);
    }

    private List getParamsForUserHttpService() {
        final List<NameValuePair> urlParams = new ArrayList<>();
        urlParams.add(new BasicNameValuePair("username", "alice"));
        urlParams.add(new BasicNameValuePair("name", "alice"));
        urlParams.add(new BasicNameValuePair("email", "alice@yale.edu"));
        return urlParams;
    }

    private HttpResponse execDelete(final String service) throws Exception {
        final HttpServiceTestUtil httpServiceTestUtil = new HttpServiceTestUtil();
        HttpDelete delete = httpServiceTestUtil.doDELETE(service);
        final HttpResponse response = httpServiceTestUtil.getHttpClient().execute(delete);
        return response;
    }
}
