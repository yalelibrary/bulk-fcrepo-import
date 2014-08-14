package edu.yale.library.ladybird.entity;


import java.util.Date;

public class RolesPermissions implements java.io.Serializable {

    private int id;
    private int roleId;
    private int permissiosnId;
    private Character value;
    private Date createdDate;

    public RolesPermissions() {
    }

    public RolesPermissions(int roleId, int permissiosnId, Character value) {
        this.roleId = roleId;
        this.permissiosnId = permissiosnId;
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

    public void setPermissiosnId(int permissiosnId) {
        this.permissiosnId = permissiosnId;
    }

    public int getRoleId() {
        return roleId;
    }

    public int getPermissiosnId() {
        return permissiosnId;
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
                + ", permissiosnId=" + permissiosnId
                + ", value=" + value
                + ", createdDate=" + createdDate
                + '}';
    }
}


