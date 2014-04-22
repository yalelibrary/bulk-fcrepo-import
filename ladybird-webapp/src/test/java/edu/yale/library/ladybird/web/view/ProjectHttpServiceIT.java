package edu.yale.library.ladybird.web.view;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.tika.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProjectHttpServiceIT extends AbstractWarTest {

    private final String HTTP_SERVICE = "project";

    @BeforeClass
    public static void setup() throws MalformedURLException {
        setupContainer();
    }

    @AfterClass
    public static void tearDown() {
        //TODO
    }

    @Test
    public void testGet() throws Exception {
        HttpServiceTestUtil httpServiceTestUtil = new HttpServiceTestUtil();
        final HttpGet getMethod0 = httpServiceTestUtil.doGET(HTTP_SERVICE);
        final HttpResponse response0 = httpServiceTestUtil.httpClient.execute(getMethod0);
        assertNotNull(response0);
        assertEquals(IOUtils.toString(response0.getEntity().getContent()), 200,
                response0.getStatusLine().getStatusCode());
    }

}
