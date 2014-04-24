package edu.yale.library.ladybird.engine.oai;

import org.w3c.dom.Node;

/**
 * MarcReader supertype
 */
public interface MarcReader {
    Record readMarc(Node arg) throws MarcReadingException;
}
