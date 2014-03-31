package edu.yale.library.ladybird.engine.oai;

import edu.yale.library.ladybird.engine.model.MarcReadingException;
import it.svario.xpathapi.jaxp.XPathAPI;
import org.xml.sax.SAXException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * OAI-PMH client. Hits the web service and gets the XML. Passes off data to MarcReader
 */
public class OaiHttpClient {
    private static final Logger logger = getLogger(OaiHttpClient.class);
    private OaiProvider oaiProvider;
    private MarcReader marcReader;
    private final PoolingHttpClientConnectionManager connectionManager;
    private final HttpClient httpClient;
    private static final int IDLE_TIMEOUT = 3;

    //TODO
    {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.SECONDS);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    public OaiHttpClient(OaiProvider oaiProvider) {
        this.oaiProvider = oaiProvider;
        this.marcReader = new DefaultMarcReader(); //TODO
    }


    /**
     * Reads Marc stream (assumes marc connection w/ bibId)
     *
     * @param bibId
     */
    public Record readMarc(final String bibId) throws IOException, MarcReadingException {
        logger.debug("Reading marc for doc={}", bibId);
        try {
            final Node node = extractMarcXml(harvest(OaiUrlHelper.urlForMarcGetRecord(oaiProvider, bibId)));
            return marcReader.readMarc(node);
        } catch (IOException e) {
            logger.error("Error reading OAI feed.", e);
            throw e;
        } catch (MarcReadingException m) {
            logger.error("Error reading marc document.", m);
            throw m;
        }
    }

    /**
     * Reads DC stream (assumes marc connection w/ barcode)
     *
     * @param barcode
     */
    public void readDC(final String barcode) {
        logger.debug("Reading dc for doc={}", barcode);
        try {
            final String xml = harvest(OaiUrlHelper.urlForDCGetRecord(oaiProvider, barcode));

        } catch (IOException e) {
            logger.error("Error reading OAI feed.", e);
        }
    }

    /**
     * Strip OAI tags surrounding marc:record
     *
     * @param xml
     * @return
     */
    private Node extractMarcXml(final String xml) throws MarcReadingException {
        try {
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final Document doc = builder.parse(IOUtils.toInputStream(xml));
            final Map<String, String> namespaceMap = new HashMap<>();
            namespaceMap.put("k", "http://www.openarchives.org/OAI/2.0/");
            namespaceMap.put("l", "http://www.loc.gov/MARC21/slim");
            final Node n = XPathAPI.selectSingleNode(doc, "//l:record", namespaceMap);
            return n;
        } catch (ParserConfigurationException | SAXException | XPathException | IOException e) {
            logger.error("Error extracting marc record", e);
            throw new MarcReadingException("Marc extracting failed", e);
        }
    }

    /**
     * Fetches contents from url.
     * TODO pre-validate before parsing content?
     *
     * @param URL
     * @return
     * @throws IOException
     */
    private String harvest(final String URL) throws IOException {
        try {
            HttpGet getRequest = new HttpGet(URL);
            final HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }
            final BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF-8"));
            final StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                //FIXME. There will be special cases such as these. Perhaps the fix is to use a different reader.
                if (!output.startsWith("<")) {
                    output = " " + output;
                }
                sb.append(output);
                //logger.debug(output);
            }
            return sb.toString();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Helps construct urls to hit the provider
     * TODO validate before returning
     */
    private static class OaiUrlHelper {
        /**
         * Support metadata formats
         */
        enum METADATA {
            marc21, dc
        }

        /**
         * OAI-PMH verbs
         */
        enum VERB {
            GetRecord
        }

        /**
         * Constants
         */
        class OAIPrefix {
            static final String VERB_PREFIX = "verb";
            static final String IDENTIFIER_PREFIX = "identifier";
            static final String METADATA_PREFIX = "metadataPrefix";
        }

        /**
         * Helper method for Marc
         *
         * @param oaiProvider
         * @param idFor
         * @return
         */
        static String urlForMarcGetRecord(final OaiProvider oaiProvider, final String idFor) {
            return getUrl(oaiProvider, idFor, VERB.GetRecord, METADATA.marc21);
        }

        /**
         * Helper method for DC
         *
         * @param oaiProvider
         * @param idFor
         * @return
         */
        static String urlForDCGetRecord(final OaiProvider oaiProvider, final String idFor) {
            return getUrl(oaiProvider, idFor, VERB.GetRecord, METADATA.dc);
        }

        static String getUrl(final OaiProvider oaiProvider, final String idFor, VERB verb, METADATA metadata) {
            URIBuilder uriBuilder = new URIBuilder();
            uriBuilder.setPath(oaiProvider.getUrl());
            uriBuilder.addParameter(OAIPrefix.VERB_PREFIX, verb.toString());
            uriBuilder.addParameter(OAIPrefix.IDENTIFIER_PREFIX, oaiProvider.getBibIdPrefix() + idFor);
            uriBuilder.addParameter(OAIPrefix.METADATA_PREFIX, metadata.toString());
            return uriBuilder.toString();
        }
    }
}
