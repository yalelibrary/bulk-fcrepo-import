package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.engine.Util;
import edu.yale.library.ladybird.engine.model.MarcReadingException;
import edu.yale.library.ladybird.engine.oai.OaiHttpClient;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.engine.oai.Record;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * OAI feed reading test (for now, against a known external OAI provider)
 */

public class OaiReaderIT {

    private static final String DC_OAI_PROVIDER = getProp("oai_test_url");
    private static final String MARC_OAI_PROVIDER = getOaiRequestVerbForMarcFeed();
    private final PoolingHttpClientConnectionManager connectionManager;
    private final HttpClient httpClient;
    private static final int IDLE_TIMEOUT = 3;

    {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    @Test
    public void shouldGetMarcFeed() {
        try {
            final String s = fetchContents(MARC_OAI_PROVIDER);
            assertEquals(StringUtils.countMatches(s, "marc:subfield"), 88);
        } catch (Exception e) {
            fail("Error retrieving OAI feed");
        }
    }

    @Test
    public void shouldGetDCFeed() {
        try {
            final String s = fetchContents(DC_OAI_PROVIDER);
            assertEquals(StringUtils.countMatches(s, "dc:subject"), 8);
        } catch (Exception e) {
            fail("Error retrieving OAI feed");
        }
    }

    @Test
    public void shouldReadMarcRecord() {
        final String bibId = getProp("oai_bibid");
        final OaiProvider provider = new OaiProvider("id", getProp("oai_test_url_prefix"),
                getProp("oai_url_id"));
        final OaiHttpClient client = new OaiHttpClient(provider);
        try {
            Record record = client.readMarc(bibId);
            assertEquals("DataField size mismatch", record.getDatafield().size(), 24);
        } catch (IOException | MarcReadingException e) {
            fail();
        }
    }

    private String fetchContents(final String URL) throws Exception {
        try {
            final HttpGet getRequest = new HttpGet(URL);
            final HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            final BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            final StringBuffer sb = new StringBuffer();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    private static String getOaiRequestVerbForMarcFeed() {
        final URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(Util.getProperty("oai_test_url_prefix"));
        uriBuilder.addParameter("verb", "GetRecord");
        uriBuilder.addParameter("identifier", getProp("oai_url_id") + getProp("oai_bibid"));
        uriBuilder.addParameter("metadataPrefix", "marc21");
        return uriBuilder.toString();
    }

    private static String getProp(final String p) {
        return Util.getProperty(p);
    }
}
