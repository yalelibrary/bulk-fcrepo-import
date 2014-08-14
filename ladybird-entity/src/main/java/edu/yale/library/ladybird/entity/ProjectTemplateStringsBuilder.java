package edu.yale.library.ladybird.entity;

public class ProjectTemplateStringsBuilder {
    private Integer templateId;
    private Integer fdid;
    private String value;

    public ProjectTemplateStringsBuilder setTemplateId(Integer templateId) {
        this.templateId = templateId;
        return this;
    }

    public ProjectTemplateStringsBuilder setFdid(Integer fdid) {
        this.fdid = fdid;
        return this;
    }

    public ProjectTemplateStringsBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public ProjectTemplateStrings createProjectTemplateStrings() {
        return new ProjectTemplateStrings(templateId, fdid, value);
    }
}