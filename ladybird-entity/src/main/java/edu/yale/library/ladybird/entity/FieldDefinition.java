package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * FieldDefinition
 */
public class FieldDefinition implements java.io.Serializable {

    private int fdid;
    private Date date;
    private String type;
    private int acid;
    private int faid;
    private String handle;
    private String tooltip;
    private int multivalue;
    private int display;
    private int technical;
    private int export;
    private int locked;
    private String required;
    private String style;

    public FieldDefinition() {
    }

    public FieldDefinition(int fdid, String handle) {
        this.fdid = fdid;
        this.handle = handle;
    }

    public int getFdid() {
        return this.fdid;
    }

    public void setFdid(int fdid) {
        this.fdid = fdid;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAcid() {
        return this.acid;
    }

    public void setAcid(int acid) {
        this.acid = acid;
    }

    public int getFaid() {
        return this.faid;
    }

    public void setFaid(int faid) {
        this.faid = faid;
    }

    public String getHandle() {
        return this.handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public int getMultivalue() {
        return this.multivalue;
    }

    public void setMultivalue(int multivalue) {
        this.multivalue = multivalue;
    }

    public int getDisplay() {
        return this.display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public int getTechnical() {
        return this.technical;
    }

    public void setTechnical(int technical) {
        this.technical = technical;
    }

    public int getExport() {
        return this.export;
    }

    public void setExport(int export) {
        this.export = export;
    }

    public int getLocked() {
        return this.locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getRequired() {
        return this.required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    public String toString() {
        return "FieldDefinition{"
                + "fdid=" + fdid
                + ", date=" + date
                + ", type='" + type + '\''
                + ", acid=" + acid
                + ", faid=" + faid
                + ", handle='" + handle + '\''
                + ", tooltip='" + tooltip + '\''
                + ", multivalue=" + multivalue
                + ", display=" + display
                + ", technical=" + technical
                + ", export=" + export
                + ", locked=" + locked
                + ", required='" + required + '\''
                + ", style='" + style + '\''
                + '}';
    }

}


