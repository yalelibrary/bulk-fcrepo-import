package edu.yale.library.engine.oai;

import edu.yale.library.engine.model.MarcReadingException;
import org.w3c.dom.Node;

/**
 * MarcReader supertype
 */
public interface MarcReader
{
    Record readMarc(Node arg) throws MarcReadingException;
}
