package edu.yale.library.ladybird.engine.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MediaFunctionProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void process(final List<ImportEntity.Row> rowList) {
        logger.debug("Processing list size={}", rowList.size());
    }
}
