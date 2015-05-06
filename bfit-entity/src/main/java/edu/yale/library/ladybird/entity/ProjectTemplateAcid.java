package edu.yale.library.ladybird.entity;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ProjectTemplateAcid implements java.io.Serializable {

    private Integer id;
    private Integer templateId;
    private Integer fdid;
    private Integer acid;

    public ProjectTemplateAcid() {
    }

    public ProjectTemplateAcid(Integer templateId, Integer fdid, Integer acid) {
        this.templateId = templateId;
        this.fdid = fdid;
        this.acid = acid;
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

    public Integer getAcid() {
        return this.acid;
    }

    public void setAcid(Integer acid) {
        this.acid = acid;
    }

}


