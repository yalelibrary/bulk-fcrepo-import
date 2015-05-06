package edu.yale.library.ladybird.engine.oai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * A simple impelmentation. Implement and Use Marc4J wrapper for more extensinve functionality.
 *
 * @author Osman Din
 */
public class DefaultMarcReader implements MarcReader {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMarcReader.class);

    public Record readMarc(final Node arg) throws MarcReadingException {
        try {
            return readContents(arg);
        } catch (JAXBException e) {
            logger.error("Exception reading marc record");
            throw new MarcReadingException(e);
        }
    }

    private Record readContents(final Node node) throws JAXBException {
        try {
            assert (node != null);
            final JAXBContext jaxbContext = JAXBContext.newInstance(Record.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Record record = (Record) jaxbUnmarshaller.unmarshal(node);
            return record;
        } catch (JAXBException e) {
            throw e;
        }
    }
}
