package edu.yale.library.ladybird.entity;


/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class Roles implements java.io.Serializable {

    private Integer roleId;
    private String roleName;
    private String roleDesc;

    public Roles() {
    }

    public Roles(String roleName, String roleDesc) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }

    public Integer getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDesc() {
        return this.roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public String toString() {
        return "Roles{"
                + "roleName='" + roleName + '\''
                + ", roleDesc='" + roleDesc + '\''
                + '}';
    }
}


