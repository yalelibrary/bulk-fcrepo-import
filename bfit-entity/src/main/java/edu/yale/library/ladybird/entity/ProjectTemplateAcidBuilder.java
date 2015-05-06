package edu.yale.library.ladybird.entity;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ProjectTemplateAcidBuilder {

    private Integer templateId;
    private Integer fdid;
    private Integer acid;

    public ProjectTemplateAcidBuilder setTemplateId(Integer templateId) {
        this.templateId = templateId;
        return this;
    }

    public ProjectTemplateAcidBuilder setFdid(Integer fdid) {
        this.fdid = fdid;
        return this;
    }

    public ProjectTemplateAcidBuilder setAcid(Integer acid) {
        this.acid = acid;
        return this;
    }

    public ProjectTemplateAcid createProjectTemplateAcid() {
        return new ProjectTemplateAcid(templateId, fdid, acid);
    }
}