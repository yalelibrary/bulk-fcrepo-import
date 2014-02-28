package test;


import library.AbstractHttpServiceTester;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.tika.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ImportHttpServiceIT extends AbstractHttpServiceTester
{

    final private String HTTP_SERVICE = "cronjobs";

    @Test
    public void testGetScheduledJobs() throws Exception
    {
        final HttpGet getMethod0 = HttpGetCall(HTTP_SERVICE);
        final HttpResponse response0 = httpClient.execute(getMethod0);
        assertNotNull(response0);
        assertEquals(IOUtils.toString(response0.getEntity().getContent()), 200,
                response0.getStatusLine().getStatusCode());
    }

}
