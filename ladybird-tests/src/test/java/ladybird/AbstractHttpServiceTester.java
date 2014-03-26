package ladybird;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.Before;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;


public abstract class AbstractHttpServiceTester {
    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    protected HttpClient httpClient;

    private static final int DEFAULT_MAX_PER_ROUTE = 5;

    private static final int IDLE_TIMEOUT = 3;

    private static final String DEFAULT_TEST_PORT = "9090";

    private Logger logger;

    protected static final int SERVER_PORT = Integer.parseInt(System
            .getProperty("test.port", DEFAULT_TEST_PORT));

    protected static final String HOSTNAME = "localhost";

    protected static final String SUFFIX = "";

    protected static final String serverAddress = "http://" + HOSTNAME + ":"
            + SERVER_PORT + "/ladybird-webapp/rest/";


    @Before
    public void init() {
        logger = getLogger(this.getClass());
    }

    //not shared
    public AbstractHttpServiceTester() {
        setConnectionManagerProps(connectionManager);
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    private static void setConnectionManagerProps(PoolingHttpClientConnectionManager cm) {
        cm.setMaxTotal(Integer.MAX_VALUE);
        cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        cm.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);
    }

    protected HttpGet HttpGetCall(final String param) {
        HttpGet get = new HttpGet(serverAddress + param + "/" + SUFFIX);
        logger.debug("GET: {}", get.getURI());
        return get;
    }

    protected HttpPost HttpPostCall(final String param) {
        HttpPost post = new HttpPost(serverAddress + param + "/" + SUFFIX);
        logger.debug("POST: {}", post.getURI());
        return post;
    }

    protected HttpDelete HttpDeleteCall(final String param) {
        HttpDelete delete = new HttpDelete(serverAddress + param + "/" + SUFFIX);
        logger.debug("DELETE: {}", delete.getURI());
        return delete;
    }
}

