package edu.yale.library.ladybird.auth;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum Roles {

    ADMIN("admin") {

        boolean setPermissions = false;

        @Override
        public List<PermissionsValue> getPermissions() {

            if (setPermissions) {
                return this.permissions;
            }
            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(Permissions.USER_ADD, true));
            permissions.add(getValue(Permissions.USER_LIST, true));
            permissions.add(getValue(Permissions.IMPORTSOURCE_ADD, true));
            permissions.add(getValue(Permissions.PROJECT_ADD, true));
            return permissions;
        }

        @Override
        public void setPermissions(List<PermissionsValue> list) {
            permissions = list;
            setPermissions = true;
        }
    },
    VISITOR("visitor") {

        boolean setPermissions = false;

        @Override
        public List<PermissionsValue> getPermissions() {

            if (setPermissions) {
                return this.permissions;
            }

            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(Permissions.USER_ADD, false));
            permissions.add(getValue(Permissions.USER_LIST, true));
            permissions.add(getValue(Permissions.IMPORTSOURCE_ADD, false));
            permissions.add(getValue(Permissions.PROJECT_ADD, false));
            return permissions;
        }

        @Override
        public void setPermissions(List<PermissionsValue> list) {
            permissions = list;
            setPermissions = true;
        }
    },
    PROJECT_ADMIN("projectadmin") {

        boolean setPermissions = false;

        public List<PermissionsValue> getPermissions() {

            if (setPermissions) {
                return this.permissions;
            }

            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(Permissions.USER_ADD, true));
            permissions.add(getValue(Permissions.USER_LIST, true));
            permissions.add(getValue(Permissions.IMPORTSOURCE_ADD, false));
            permissions.add(getValue(Permissions.PROJECT_ADD, false));
            return permissions;
        }

        @Override
        public void setPermissions(List<PermissionsValue> list) {
            permissions = list;
            setPermissions = true;
        }
    },
    PROJECT_USER("projectuser") {

        boolean setPermissions = false;

        public List<PermissionsValue> getPermissions() {

            if (setPermissions) {
                return this.permissions;
            }

            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(Permissions.USER_ADD, false));
            permissions.add(getValue(Permissions.USER_LIST, true));
            permissions.add(getValue(Permissions.IMPORTSOURCE_ADD, false));
            permissions.add(getValue(Permissions.PROJECT_ADD, false));
            permissions.add(getValue(Permissions.PROJECT_DELETE, false));
            return permissions;
        }

        @Override
        public void setPermissions(List<PermissionsValue> list) {
            permissions = list;
            setPermissions = true;
        }
    },
    NONE("none") {
        public List<PermissionsValue> getPermissions() {
            final List<PermissionsValue> permissionsList = new ArrayList<>();
            final Permissions[] permissions = Permissions.values();

            for (final Permissions p: permissions) {
                permissionsList.add(new PermissionsValue(p, false));
            }
            return permissionsList;
        }
    };

    private String name = "";

    protected List<PermissionsValue> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Roles(String name) {
        this.name = name;
    }

    public List<PermissionsValue> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionsValue> permissions) {
        this.permissions = permissions;
    }

    private static PermissionsValue getValue(final Permissions p, final boolean hasPermission) {
        return new PermissionsValue(p, hasPermission);
    }

    public static Roles fromString(final String s) {
        if (s != null) {
            for (Roles r : Roles.values()) {
                if (s.equalsIgnoreCase(r.name)) {
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Roles{"
                + "name='" + name + '\''
                + ", permissions=" + permissions
                + '}';
    }
}
