package edu.yale.library.ladybird.engine.model;

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
}
