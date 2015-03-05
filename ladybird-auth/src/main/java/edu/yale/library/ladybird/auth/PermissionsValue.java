package edu.yale.library.ladybird.auth;


/**
 * Stored to use values, and possibly other attributes (hence not a map).
 */
public class PermissionsValue {

    private PermissionSet permissionSet;

    private boolean enabled;

    public PermissionsValue(PermissionSet permissionSet, boolean enabled) {
        this.permissionSet = permissionSet;
        this.enabled = enabled;
    }

    public PermissionSet getPermissionSet() {
        return permissionSet;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPermissionSet(PermissionSet permissionSet) {
        this.permissionSet = permissionSet;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "PermissionsValue{"
                + "permissions=" + permissionSet
                + ", enabled=" + enabled
                + '}';
    }
}
