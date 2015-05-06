package edu.yale.library.ladybird.entity;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class Permissions implements java.io.Serializable {

    private Integer permissionsId;
    private String permissionsName;
    private String permissionsDesc;

    public Permissions() {
    }

    public Permissions(String permissionsName, String permissionsDesc) {
        this.permissionsName = permissionsName;
        this.permissionsDesc = permissionsDesc;
    }

    public Integer getPermissionsId() {
        return this.permissionsId;
    }

    public void setPermissionsId(Integer permissionsId) {
        this.permissionsId = permissionsId;
    }

    public String getPermissionsName() {
        return this.permissionsName;
    }

    public void setPermissionsName(String permissionsName) {
        this.permissionsName = permissionsName;
    }

    public String getPermissionsDesc() {
        return this.permissionsDesc;
    }

    public void setPermissionsDesc(String permissionsDesc) {
        this.permissionsDesc = permissionsDesc;
    }

    @Override
    public String toString() {
        return "Permissions{"
                + "permissionsName='" + permissionsName + '\''
                + ", permissionsDesc='" + permissionsDesc + '\''
                + '}';
    }
}


