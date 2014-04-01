package edu.yale.library.ladybird.tests;


import edu.yale.library.ladybird.engine.model.MarcReadingException;
import edu.yale.library.ladybird.engine.oai.OaiHttpClient;
import edu.yale.library.ladybird.engine.oai.OaiProvider;
import edu.yale.library.ladybird.engine.oai.Record;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Exercies abiltiy to read Record instances from a live OAI feed.
 *
 * @see OaiReaderIT for comments on testing live feeds
 * @see Record
 * @see OaiHttpClient
 */
public class OaiHttpClientTest {

    @Test
    public void testReadMarc() {
        final String bibId = getProp("oai_bibid");
        OaiProvider o = new OaiProvider("id", getProp("oai_test_url_prefix"),
                getProp("oai_url_id"));
        OaiHttpClient client = new OaiHttpClient(o);
        try {
            Record record = client.readMarc(bibId);
            assertEquals("DataField size mismatch", record.getDatafield().size(), 24);
        } catch (IOException | MarcReadingException e) {
            fail();
        }
    }

    private String getProp(final String p) {
        return Util.getProperty(p);
    }

}
