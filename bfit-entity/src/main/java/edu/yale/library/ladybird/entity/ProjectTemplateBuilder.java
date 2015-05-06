package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ProjectTemplateBuilder {

    private Date date;
    private Integer creator;
    private String label;
    private Integer projectId;

    public ProjectTemplateBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public ProjectTemplateBuilder setCreator(Integer creator) {
        this.creator = creator;
        return this;
    }

    public ProjectTemplateBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public ProjectTemplateBuilder setProjectId(Integer projectId) {
        this.projectId = projectId;
        return this;
    }

    public ProjectTemplate createProjectTemplate() {
        return new ProjectTemplate(date, creator, label, projectId);
    }
}