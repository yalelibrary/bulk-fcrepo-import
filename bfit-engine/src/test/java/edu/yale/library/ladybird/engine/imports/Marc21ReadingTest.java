package edu.yale.library.ladybird.engine.imports;


import edu.yale.library.ladybird.engine.oai.Record;
import it.svario.xpathapi.jaxp.XPathAPI;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests against a document
 */
public class Marc21ReadingTest {

    @Test
    public void shouldReadContents() {
        try {
            final InputStream is = this.getClass().getClassLoader().getResourceAsStream("oai/marc21_doc.xml");
            assert (is != null);
            final JAXBContext jaxbContext = JAXBContext.newInstance(Record.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Record record = (Record) jaxbUnmarshaller.unmarshal(is);
            assertEquals("DataField size mismatch", record.getDatafield().size(), 24);
        } catch (JAXBException e) {
            fail("Reading test marc document failed");
        }
    }

    /**
     * Tests full feed, and extract marc:record element
     */
    @Test
    public void shouldUnmarshallDataFields() {
        try {
            final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            final DocumentBuilder builder = domFactory.newDocumentBuilder();
            final Document doc = builder.parse(this.getClass().getClassLoader()
                    .getResourceAsStream("oai/oai_marc21"));
            final Map<String, String> namespaceMap = new HashMap<>();
            namespaceMap.put("k", "http://www.openarchives.org/OAI/2.0/");
            namespaceMap.put("l", "http://www.loc.gov/MARC21/slim");
            final Node node = XPathAPI.selectSingleNode(doc, "//l:record", namespaceMap);
            final Record r = asRecord(node);
            assertEquals("mismatch", r.getDatafield().size(), 24);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test that the entity can be retrieved
     *
     * @param p
     * @throws JAXBException
     */
    public Record asRecord(Node p) throws JAXBException {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(Record.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Record record = (Record) jaxbUnmarshaller.unmarshal(p);
            assertEquals("Record size mismatch", record.getDatafield().size(), 24);
            return record;
        } catch (JAXBException e) {
            throw e;
        }
    }
}
