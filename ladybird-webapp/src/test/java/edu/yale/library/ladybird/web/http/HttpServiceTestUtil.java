package edu.yale.library.ladybird.web.http;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

public class HttpServiceTestUtil {

    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    protected HttpClient httpClient;
    private static final int DEFAULT_MAX_PER_ROUTE = 5;
    private static final int IDLE_TIMEOUT = 3;
    private static final String DEFAULT_TEST_PORT = getProp("test.port");
    private static final int SERVER_PORT = Integer.parseInt(DEFAULT_TEST_PORT);

    private static final String appUrl = "http://" + getProp("host") + ":"
            + SERVER_PORT + getProp("context.path") + "/rest/";

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
        final String url = appUrl + param + "/";
        HttpGet get = new HttpGet(url);
        return get;
    }

    private static String getProp(String s) {
        return System.getProperty(s);
    }
}

