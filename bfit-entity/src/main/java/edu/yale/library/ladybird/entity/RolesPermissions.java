package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class RolesPermissions implements java.io.Serializable {

    private int id;
    private int roleId;
    private int permissionsId;
    private Character value;
    private Date createdDate;

    public RolesPermissions() {
    }

    public RolesPermissions(int roleId, int permissionsId, Character value) {
        this.roleId = roleId;
        this.permissionsId = permissionsId;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setPermissionsId(int permissionsId) {
        this.permissionsId = permissionsId;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getPermissionsId() {
        return permissionsId;
    }


    public Character getValue() {
        return this.value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "RolesPermissions{"
                + "roleId=" + roleId
                + ", permissionsId=" + permissionsId
                + ", value=" + value
                + ", createdDate=" + createdDate
                + '}';
    }
}


