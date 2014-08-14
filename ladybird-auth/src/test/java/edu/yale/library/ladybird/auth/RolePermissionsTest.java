package edu.yale.library.ladybird.auth;

import org.junit.Test;

import java.util.Collections;

public class RolePermissionsTest {

    @Test
    public void shouldEqualAssignedValue() {
        final Roles adminRole = Roles.ADMIN;
        final PermissionsValue permissionsValue = new PermissionsValue(Permissions.USER_ADD, true);
        adminRole.setPermissions(Collections.singletonList(permissionsValue));

        final PermissionsValue readBack = adminRole.getPermissions().get(0);

        assert (readBack.isEnabled());
        assert (readBack.getPermissions().getName().equals(Permissions.USER_ADD.getName()));

        final Roles visitorRole = Roles.VISITOR;
        final PermissionsValue permissionsValue2 = new PermissionsValue(Permissions.USER_ADD, false);
        visitorRole.setPermissions(Collections.singletonList(permissionsValue2));

        final PermissionsValue readBack2 = visitorRole.getPermissions().get(0);

        assert (!readBack2.isEnabled());
        assert (readBack2.getPermissions().getName().equals(Permissions.USER_ADD.getName()));
    }
}
