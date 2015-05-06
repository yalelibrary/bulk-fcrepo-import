package edu.yale.library.ladybird.entity;


import java.lang.Object;
import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class FieldDefinition implements java.io.Serializable, FieldConstant, Comparable {

    private int fdid;

    //TODO date changed?
    private Date date;

    /** TODO probably ftid (string, longstring, dropdown) */
    private String type;

    /** TODO for matching with other acid?*/
    private int acid;

    //TODO?
    private int faid;

    /** TODO Assuming name */
    private String handle;

    //TODO remove?
    private String tooltip;

    //TODO helper
    private boolean multivalue;

    //TODO remove?
    private int display;

    //TODO remove?
    private int technical;

    //TODO purpose?
    private int export;

    //TODO
    //private boolean locked;
    //private boolean required;

    //TODO remove?
    private String style;

    public FieldDefinition() {
    }
    public FieldDefinition(int fdid) {
        this.fdid = fdid;
    }

    public FieldDefinition(int fdid, String handle) {
        this.fdid = fdid;
        this.handle = handle;
    }

    public FieldDefinition(final int fdid, final String handle, final Date date) {
        this.fdid = fdid;
        this.handle = handle;
        this.date = date;
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

    public boolean isMultivalue() {
        return multivalue;
    }

    public void setMultivalue(boolean multivalue) {
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

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setExport(int export) {
        this.export = export;
    }

    @Override
    public String toString() {
        return "FieldDefinition{"
                + "fdid=" + fdid
                //+ ", date=" + date
                //+ ", type='" + type + '\''
                + ", acid=" + acid
                //+ ", faid=" + faid
                + ", handle='" + handle + '\''
                //+ ", tooltip='" + tooltip + '\''
                //+ ", multivalue=" + multivalue
                //+ ", display=" + display
                //+ ", technical=" + technical
                //+ ", export=" + export
                //+ ", locked=" + locked
                //+ ", required='" + required + '\''
                //+ ", style='" + style + '\''
                + '}';
    }

    @Override
    public String getName() {
        return "fdid=" + fdid;
    }

    @Override
    public void setName(final String s) {
        throw new UnsupportedOperationException("Cannot set this value for FieldDefintion");
    }

    @Override
    public String getTitle() {
        return handle;
    }

    @Override
    public void setTitle(final String s) {
        throw new UnsupportedOperationException("Cannot set this value for FieldDefintion");
    }

    //todo
    @Override
    public int compareTo(Object o) {
        FieldDefinition f = (FieldDefinition) o;

        if (f.fdid == this.fdid) {
            return 0;
        }

        return f.fdid < this.fdid ? 1 : -1;
    }

    /**
     * converter helper
     * FIXME this depends on the the fdids are loaded (currently through a file fdid.test.propeties at webapp start up)
     *
     * @param s string e.g. from Host, note{fdid=68}
     * @return integer value
     */
    public static Integer fdidAsInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            String[] parsedString = s.split("fdid=");
            return Integer.parseInt(parsedString[1].replace("}", ""));
        }
    }

}


