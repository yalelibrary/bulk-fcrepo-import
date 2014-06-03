package edu.yale.library.ladybird.entity;


import java.util.Date;

/**
 * ObjectFile
 */
public class ObjectFile implements java.io.Serializable {


    private Integer dataId;
    private Date date;
    private int userId;
    private int oid;
    private String fileLabel;
    private String fileName;
    private String filePath;
    private String fileExt;
    private Integer fileSize;
    private String md5;
    private String sha256;
    private Integer hydraPublishId;
    private String status;

    public ObjectFile() {
    }

    public Integer getDataId() {
        return this.dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOid() {
        return this.oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getFileLabel() {
        return this.fileLabel;
    }

    public void setFileLabel(String fileLabel) {
        this.fileLabel = fileLabel;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileExt() {
        return this.fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public Integer getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha256() {
        return this.sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public Integer getHydraPublishId() {
        return this.hydraPublishId;
    }

    public void setHydraPublishId(Integer hydraPublishId) {
        this.hydraPublishId = hydraPublishId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ObjectFile{"
                + "dataId=" + dataId
                + ", date=" + date
                + ", userId=" + userId
                + ", oid=" + oid
                + ", fileLabel='" + fileLabel + '\''
                + ", fileName='" + fileName + '\''
                + ", filePath='" + filePath + '\''
                + ", fileExt='" + fileExt + '\''
                + ", fileSize=" + fileSize
                + ", md5='" + md5 + '\''
                + ", sha256='" + sha256 + '\''
                + ", hydraPublishId=" + hydraPublishId
                + ", status='" + status + '\''
                + '}';
    }
}


