package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ImportSource implements java.io.Serializable {
    private Integer importSourceId;
    private Date createdDate;
    private String url;
    private String xmlType;
    private String getPrefix;
    private boolean active;

    public ImportSource() {
    }


    public ImportSource(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public ImportSource(final Date createdDate, final String url, final String xmlType, final String getPrefix) {
        this.createdDate = createdDate;
        this.url = url;
        this.xmlType = xmlType;
        this.getPrefix = getPrefix;
    }

    public Integer getImportSourceId() {
        return this.importSourceId;
    }

    public void setImportSourceId(final Integer importSourceId) {
        this.importSourceId = importSourceId;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getXmlType() {
        return this.xmlType;
    }

    public void setXmlType(final String xmlType) {
        this.xmlType = xmlType;
    }

    public String getGetPrefix() {
        return this.getPrefix;
    }

    public void setGetPrefix(final String getPrefix) {
        this.getPrefix = getPrefix;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "ImportSource{"
                + "importSourceId=" + importSourceId
                + ", createdDate=" + createdDate
                + ", url='" + url + '\''
                + ", xmlType=" + xmlType
                + ", getPrefix='" + getPrefix + '\''
                + ", active='" + active + '\''
                + '}';
    }
}


