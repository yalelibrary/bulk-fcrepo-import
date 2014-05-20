package edu.yale.library.ladybird.kernel.events;

/**
 * Standard ladybird events
 */
public enum Events {
    USER_LOGIN("user.login"),
    USER_VISIT("user.visit.page"),
    USER_SEARCH("user.search");

    String name;

    private Events(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
