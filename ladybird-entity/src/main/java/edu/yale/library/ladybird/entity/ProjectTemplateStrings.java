package edu.yale.library.ladybird.entity;

public class ProjectTemplateStrings implements java.io.Serializable {

    private Integer id;
    private Integer templateId;
    private Integer fdid;
    private String value;

    public ProjectTemplateStrings() {
    }

    public ProjectTemplateStrings(Integer templateId, Integer fdid, String value) {
        this.templateId = templateId;
        this.fdid = fdid;
        this.value = value;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getFdid() {
        return this.fdid;
    }

    public void setFdid(Integer fdid) {
        this.fdid = fdid;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ProjectTemplateStrings{"
                + "id=" + id
                + ", templateId=" + templateId
                + ", fdid=" + fdid
                + ", value='" + value + '\''
                + '}';
    }
}


