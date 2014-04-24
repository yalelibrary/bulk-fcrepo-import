package edu.yale.library.entity.model;

import java.util.Date;

public class FieldDefinitionBuilder {
    private int fdid;
    private String handle;
    private Date date;
    private String type;
    private int acid;
    private int faid;
    private String tooltip;
    private int multivalue;
    private int display;
    private int technical;
    private int export;
    private int locked;
    private String required;
    private String style;

    public FieldDefinitionBuilder setFdid(int fdid) {
        this.fdid = fdid;
        return this;
    }

    public FieldDefinitionBuilder setHandle(String handle) {
        this.handle = handle;
        return this;
    }

    public FieldDefinitionBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public FieldDefinitionBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public FieldDefinitionBuilder setAcid(int acid) {
        this.acid = acid;
        return this;
    }

    public FieldDefinitionBuilder setFaid(int faid) {
        this.faid = faid;
        return this;
    }

    public FieldDefinitionBuilder setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public FieldDefinitionBuilder setMultivalue(int multivalue) {
        this.multivalue = multivalue;
        return this;
    }

    public FieldDefinitionBuilder setDisplay(int display) {
        this.display = display;
        return this;
    }

    public FieldDefinitionBuilder setTechnical(int technical) {
        this.technical = technical;
        return this;
    }

    public FieldDefinitionBuilder setExport(int export) {
        this.export = export;
        return this;
    }

    public FieldDefinitionBuilder setLocked(int locked) {
        this.locked = locked;
        return this;
    }

    public FieldDefinitionBuilder setRequired(String required) {
        this.required = required;
        return this;
    }

    public FieldDefinitionBuilder setStyle(String style) {
        this.style = style;
        return this;
    }

    public FieldDefinition createFieldDefinition() {
        return new FieldDefinition(fdid, handle);
    }
}