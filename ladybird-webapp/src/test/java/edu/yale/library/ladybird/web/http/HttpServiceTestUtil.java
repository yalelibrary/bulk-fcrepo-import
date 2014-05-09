package edu.yale.library.ladybird.web.http;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
    private static final String REST = "/rest/";
    private static final String appUrl = buildAppRestUrl();

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

    public HttpPost doPOST(final String param) {
        final String url = appUrl + param + "/";
        HttpPost post = new HttpPost(url);
        return post;
    }

    public HttpDelete doDELETE(final String param) {
        final String url = appUrl + param + "/";
        HttpDelete delete = new HttpDelete(url);
        return delete;
    }

    private static String getProp(String s) {
        return System.getProperty(s);
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    private static String buildAppRestUrl() {
        return "http://" + getProp("host") + ":" + SERVER_PORT + getProp("context.path") + REST;
    }
}

