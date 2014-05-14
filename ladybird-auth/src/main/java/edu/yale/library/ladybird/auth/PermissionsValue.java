package edu.yale.library.ladybird.auth;


public class PermissionsValue {

    private Permissions permissions;
    private boolean enabled;

    public PermissionsValue(Permissions permissions, boolean enabled) {
        this.permissions = permissions;
        this.enabled = enabled;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "PermissionsValue{"
                + "permissions=" + permissions
                + ", enabled=" + enabled
                + '}';
    }
}
