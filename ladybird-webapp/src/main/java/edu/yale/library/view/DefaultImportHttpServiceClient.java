package edu.yale.library.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * Simple HTTP client. This class will be replaced with a proxy class for service.
 */

@ManagedBean
@RequestScoped
public class DefaultImportHttpServiceClient
{
    private final static Logger logger = getLogger(DaoInitializer.class);

    private final static String URL = "http://localhost:8080/ladybird-webapp/rest/cronjobs"; //TODO

    private final PoolingHttpClientConnectionManager connectionManager;

    private final HttpClient httpClient;

    private static final int IDLE_TIMEOUT = 3;

    {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);

        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    /**
     * //TODO replace this
     * Prints all jobs as string
     *
     * @return
     */
    public String getJobs()
    {
        try
        {
            HttpGet getRequest = new HttpGet(URL);
            getRequest.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200)
            {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));
            StringBuffer sb = new StringBuffer();
            String output = "";
            while ((output = br.readLine()) != null)
            {
                sb.append(output);
            }

            return sb.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "Error in service.";
        }
        finally
        {
            //TODO shutdown; default is idle timeout
        }
    }
}
