package edu.yale.library.ladybird.web.http;


import edu.yale.library.ladybird.web.AbstractWarTest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.tika.io.IOUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class UsersHttpServiceIT extends AbstractWarTest {

    private final String HTTP_SERVICE = "users";

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
    public void shouldGet() throws Exception {
        final HttpResponse response0 = getHttpUser();
        assertNotNull(response0);
        assertEquals(IOUtils.toString(response0.getEntity().getContent()), 200,
                response0.getStatusLine().getStatusCode());
    }

    private void createHttpUser(final String service, final List<NameValuePair> urlParameters) throws Exception {
        final HttpServiceTestUtil httpServiceTestUtil = new HttpServiceTestUtil();
        final HttpPost post = httpServiceTestUtil.doPOST(service);

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        final HttpResponse response = httpServiceTestUtil.getHttpClient().execute(post);
        assert (response.getStatusLine().getStatusCode() == 200);
    }

    private HttpResponse getHttpUser() throws Exception {
        HttpServiceTestUtil httpServiceTestUtil = new HttpServiceTestUtil();
        final HttpGet getMethod = httpServiceTestUtil.doGET(HTTP_SERVICE);
        final HttpResponse response = httpServiceTestUtil.getHttpClient().execute(getMethod);
        return response;
    }

    private HttpResponse execDelete(final String service) throws Exception {
        final HttpServiceTestUtil httpServiceTestUtil = new HttpServiceTestUtil();
        final HttpDelete delete = httpServiceTestUtil.doDELETE(service);
        final HttpResponse response = httpServiceTestUtil.getHttpClient().execute(delete);
        return response;
    }
}
