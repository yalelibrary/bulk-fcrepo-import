package edu.yale.library.beans;


/**
 * Object
 */
public class Object implements java.io.Serializable {


    private Integer oid;
    private int projectId;

    public Object() {
    }

    public Object(int projectId) {
        this.projectId = projectId;
    }

    public Integer getOid() {
        return this.oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public int getProjectId() {
        return this.projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


}


