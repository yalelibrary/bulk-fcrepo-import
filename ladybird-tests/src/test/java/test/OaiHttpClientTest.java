package test;


import edu.yale.library.engine.model.MarcReadingException;
import edu.yale.library.engine.oai.OaiHttpClient;
import edu.yale.library.engine.oai.OaiProvider;
import edu.yale.library.engine.oai.Record;
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
        final String bibId = "6915810";
        OaiProvider o = new OaiProvider("id", "http://columbus.library.yale.edu:8055/OAI_Orbis/src/OAIOrbisTool.jsp",
                "oai:orbis.library.yale.edu:");
        OaiHttpClient client = new OaiHttpClient(o);
        try {
            Record record = client.readMarc(bibId);
            assertEquals("DataField size mismatch", record.getDatafield().size(), 24);
        } catch (IOException | MarcReadingException e) {
            fail();
        }
    }

}
