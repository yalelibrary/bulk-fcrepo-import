package ladybird;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * Generic OAI feed reading test
 * <p/>
 * FIXME decide to test live OAI urls, versus reading some xml from test resources
 * (if live urls are to be used, should probably have this as an IT with a test OAI provider)
 */


public class OaiReaderIT {
    private static final Logger logger = getLogger(OaiReaderIT.class);
    private static final String DC_OAI_PROVIDER = "http://columbus.library.yale.edu:8055/OAI_Orbis"
            + "/src/OAIOrbisTool.jsp?verb=GetRecord&identifier=oai:orbis.library.yale.edu:6915810&metadataPrefix=oai_dc";
    private static final String MARC_OAI_PROVIDER = getMarcString();
    private final PoolingHttpClientConnectionManager connectionManager;
    private final HttpClient httpClient;
    private static final int IDLE_TIMEOUT = 3;

    {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    /**
     * Raw string test
     *
     * @see OaiHttpClientTest
     */
    @Test
    public void testOAI_MARC21() {
        try {
            final String s = fetchContents(MARC_OAI_PROVIDER);
            assertEquals(StringUtils.countMatches(s, "marc:subfield"), 88); //TODO see class comment
        } catch (Exception e) {
            fail("Error retreiving OAI feed");
        }
    }

    @Test
    public void testOAI_DC() {
        try {
            final String s = fetchContents(DC_OAI_PROVIDER);
            assertEquals(StringUtils.countMatches(s, "dc:subject"), 8); //TODO see class comment
        } catch (Exception e) {
            fail("Error retreiving OAI feed");
        }
    }

    private String fetchContents(final String URL) throws Exception {
        try {
            HttpGet getRequest = new HttpGet(URL);
            getRequest.addHeader("accept", "application/json"); //TODO remove
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            StringBuffer sb = new StringBuffer();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    public static String getMarcString() {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath("http://columbus.library.yale.edu:8055/OAI_Orbis/src/OAIOrbisTool.jsp");
        uriBuilder.addParameter("verb", "GetRecord");
        uriBuilder.addParameter("identifier", "oai:orbis.library.yale.edu:" + "6915810");
        uriBuilder.addParameter("metadataPrefix", "marc21");
        return uriBuilder.toString();
    }


}
