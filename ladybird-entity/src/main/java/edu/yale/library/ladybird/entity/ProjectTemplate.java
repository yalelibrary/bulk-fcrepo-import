package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ProjectTemplate implements java.io.Serializable {

    private Integer templateId;
    private Date date;
    private Integer creator;
    private String label;
    private Integer projectId;

    public ProjectTemplate() {
    }

    public ProjectTemplate(Date date, Integer creator, String label, Integer projectId) {
        this.date = date;
        this.creator = creator;
        this.label = label;
        this.projectId = projectId;
    }

    public Integer getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getCreator() {
        return this.creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "ProjectTemplate{"
                + "templateId=" + templateId
                + ", date=" + date
                + ", creator=" + creator
                +  ", label='" + label + '\''
                +  ", projectId=" + projectId
                +  '}';
    }
}


