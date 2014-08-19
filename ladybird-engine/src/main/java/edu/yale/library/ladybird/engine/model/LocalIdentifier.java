package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportEntityValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * LocalIdentifier represents a barcode or bibid.
 * @param <T>
 */
public class LocalIdentifier<T> {

    private static Logger logger = LoggerFactory.getLogger(LocalIdentifier.class);

    private T id;

    public LocalIdentifier(final T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    public void setId(final T id) {
        this.id = id;
    }

    /**
     * Convets a list of columns to a list of LocalIdentifier (barcode or bibids)
     * @param importEntityValue representing list of columns
     * @return list of LocalIdentifier
     */
    public static List<LocalIdentifier<String>> getLocalIdList(ImportEntityValue importEntityValue) {
        List<String> list = importEntityValue.getColumnStrings(FunctionConstants.F104);

        if (list.isEmpty()) { //try F105 TODO
            list = importEntityValue.getColumnStrings(FunctionConstants.F105);
        }

        List<LocalIdentifier<String>> listLocalIds = new ArrayList<>();

        for (String s: list) {
            if (s == null || s.isEmpty()) { //TODO regex
                logger.debug("Ignoring={}", s);
                continue;
            }
            LocalIdentifier localIdentifier = new LocalIdentifier(s);
            listLocalIds.add(localIdentifier);
        }

        return listLocalIds;
    }

}
