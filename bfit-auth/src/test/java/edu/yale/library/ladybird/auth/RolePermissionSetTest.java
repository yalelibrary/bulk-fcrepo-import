package edu.yale.library.ladybird.auth;

import org.junit.Test;

import java.util.Collections;

public class RolePermissionSetTest {

    @Test
    public void shouldEqualAssignedValue() {
        final RoleSet adminRole = RoleSet.ADMIN;
        final PermissionsValue permissionsValue = new PermissionsValue(PermissionSet.USER_ADD, true);
        adminRole.setPermissions(Collections.singletonList(permissionsValue));

        final PermissionsValue readBack = adminRole.getPermissions().get(0);

        assert (readBack.isEnabled());
        assert (readBack.getPermissionSet().getName().equals(PermissionSet.USER_ADD.getName()));

        final RoleSet visitorRole = RoleSet.VISITOR;
        final PermissionsValue permissionsValue2 = new PermissionsValue(PermissionSet.USER_ADD, false);
        visitorRole.setPermissions(Collections.singletonList(permissionsValue2));

        final PermissionsValue readBack2 = visitorRole.getPermissions().get(0);

        assert (!readBack2.isEnabled());
        assert (readBack2.getPermissionSet().getName().equals(PermissionSet.USER_ADD.getName()));
    }
}
