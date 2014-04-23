package edu.yale.library.ladybird.web.http;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class HttpServiceTestUtil {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    public HttpClient httpClient;
    private static final int DEFAULT_MAX_PER_ROUTE = 5;
    private static final int IDLE_TIMEOUT = 3;
    private static final String DEFAULT_TEST_PORT = "8081";
    protected static final int SERVER_PORT = Integer.parseInt(System
            .getProperty("test.port", DEFAULT_TEST_PORT));
    protected static final String HOSTNAME = "localhost";
    protected static final String SUFFIX = "";
    protected static final String serverAddress = "http://" + HOSTNAME + ":"
            + SERVER_PORT + "/ladybird-webapp-0.0.1-SNAPSHOT/rest/";

    public HttpServiceTestUtil() {
        setConnectionManagerProps(connectionManager);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    private static void setConnectionManagerProps(final PoolingHttpClientConnectionManager cm) {
        cm.setMaxTotal(Integer.MAX_VALUE);
        cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        cm.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);
    }

    public HttpGet doGET(final String param) {
        final String url = serverAddress + param + "/" + SUFFIX;
        logger.debug("Hitting http url={}", url);
        HttpGet get = new HttpGet(url);
        return get;
    }
}

