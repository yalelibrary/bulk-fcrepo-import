package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.engine.imports.ImportEntityValue;

import java.util.ArrayList;
import java.util.List;

public class LocalIdentifier<T> {

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

    public static List<LocalIdentifier<String>> getBibIds(ImportEntityValue importEntityValue) {
        List<String> list = importEntityValue.getColumnStrings(FunctionConstants.F104);
        List<LocalIdentifier<String>> listLocalIds = new ArrayList<>();

        for (String s: list) {
            LocalIdentifier localIdentifier = new LocalIdentifier(s);
            listLocalIds.add(localIdentifier);
        }

        return listLocalIds;
    }

}
