package edu.yale.library.ladybird.auth;

/**
 *  Permissions. Must keep Roles up-to-date if a new Permissions is added.
 *  @see Roles
 */
public enum Permissions {
    USER_ADD("user.add"),
    USER_DELETE("user.remove"),
    USER_LIST("user.list"),
    USER_UPDATE("user.update"),
    PROJECT_ADD("project.add"),
    PROJECT_DELETE("project.remove"),
    IMPORTSOURCE_ADD("importsource.add"),
    IMPORTSOURCE_DELETE("importsource.remove"),
    FDID_ADD("fdid.add"),
    ACID_ADD("acid.add");

    private String name;

    private Permissions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Permissions{"
                + "name='" + name + '\''
                + '}';
    }
}
