package edu.yale.library.ladybird.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Roles and initial permissions for roles.
 * (Permissions should be allowed to change dynamically.)
 */
public enum RoleSet {

    ADMIN("admin") {

        @Override
        public List<PermissionsValue> getPermissions() {
            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(PermissionSet.USER_ADD, true));
            permissions.add(getValue(PermissionSet.USER_LIST, true));
            permissions.add(getValue(PermissionSet.IMPORTSOURCE_ADD, true));
            permissions.add(getValue(PermissionSet.PROJECT_ADD, true));
            permissions.add(getValue(PermissionSet.ACID_ADD, true));
            permissions.add(getValue(PermissionSet.FDID_ADD, true));
            return permissions;
        }
    },
    VISITOR("visitor") {

        @Override
        public List<PermissionsValue> getPermissions() {
            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(PermissionSet.USER_ADD, false));
            permissions.add(getValue(PermissionSet.USER_LIST, true));
            permissions.add(getValue(PermissionSet.IMPORTSOURCE_ADD, false));
            permissions.add(getValue(PermissionSet.PROJECT_ADD, false));
            permissions.add(getValue(PermissionSet.ACID_ADD, false));
            permissions.add(getValue(PermissionSet.FDID_ADD, false));
            return permissions;
        }
    },
    PROJECT_ADMIN("projectadmin") {

        public List<PermissionsValue> getPermissions() {
            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(PermissionSet.USER_ADD, true));
            permissions.add(getValue(PermissionSet.USER_LIST, true));
            permissions.add(getValue(PermissionSet.IMPORTSOURCE_ADD, false));
            permissions.add(getValue(PermissionSet.PROJECT_ADD, false));
            permissions.add(getValue(PermissionSet.ACID_ADD, true));
            permissions.add(getValue(PermissionSet.FDID_ADD, true));
            return permissions;
        }
    },
    PROJECT_USER("projectuser") {

        public List<PermissionsValue> getPermissions() {
            List<PermissionsValue> permissions = new ArrayList<>();
            permissions.add(getValue(PermissionSet.USER_ADD, false));
            permissions.add(getValue(PermissionSet.USER_LIST, true));
            permissions.add(getValue(PermissionSet.IMPORTSOURCE_ADD, false));
            permissions.add(getValue(PermissionSet.PROJECT_ADD, false));
            permissions.add(getValue(PermissionSet.PROJECT_DELETE, false));
            permissions.add(getValue(PermissionSet.ACID_ADD, false));
            permissions.add(getValue(PermissionSet.FDID_ADD, false));
            return permissions;
        }
    },
    NONE("none") {
        public List<PermissionsValue> getPermissions() {
            final List<PermissionsValue> permissionsList = new ArrayList<>();
            final PermissionSet[] permissions = PermissionSet.values();

            for (final PermissionSet p: permissions) {
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

    private RoleSet(String name) {
        this.name = name;
    }

    public List<PermissionsValue> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionsValue> permissions) {
        this.permissions = permissions;
    }

    private static PermissionsValue getValue(final PermissionSet p, final boolean hasPermission) {
        return new PermissionsValue(p, hasPermission);
    }

    public static RoleSet fromString(final String s) {
        if (s != null) {
            for (RoleSet r : RoleSet.values()) {
                if (s.equalsIgnoreCase(r.name)) {
                    return r;
                }
            }
        }
        return null;
    }

    private static int getRolesPermissionsSize() {
        int count = 0;
        RoleSet[] roles =  RoleSet.values();
        for (RoleSet r: roles) {
            count += r.getPermissions().size();
        }
        return count;
    }

    @Override
    public String toString() {
        return "Roles{"
                + "name='" + name + '\''
                + ", permissions=" + permissions
                + '}';
    }
}
