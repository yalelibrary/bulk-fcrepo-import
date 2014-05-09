package edu.yale.library.ladybird.web.view;

/**
 * Should match faces-config, e.g.
 *
 * <code>
 * <navigation-case>
 *     <from-outcome>ok</from-outcome>
 * </navigation-case>
 * </code>
 */
public enum NavigationCase {

    OK("ok"), FAIL("failed");

    String name;

    NavigationCase(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
