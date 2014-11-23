package edu.yale.library.ladybird.entity;

import java.util.Date;

public class ObjectFileBuilder {
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
    private byte[] thumbnail;

    public ObjectFileBuilder setDataId(final Integer dataId) {
        this.dataId = dataId;
        return this;
    }

    public ObjectFileBuilder setDate(final Date date) {
        this.date = date;
        return this;
    }

    public ObjectFileBuilder setUserId(final int userId) {
        this.userId = userId;
        return this;
    }

    public ObjectFileBuilder setOid(final int oid) {
        this.oid = oid;
        return this;
    }

    public ObjectFileBuilder setFileLabel(final String fileLabel) {
        this.fileLabel = fileLabel;
        return this;
    }

    public ObjectFileBuilder setFileName(final String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ObjectFileBuilder setFilePath(final String filePath) {
        this.filePath = filePath;
        return this;
    }

    public ObjectFileBuilder setFileExt(final String fileExt) {
        this.fileExt = fileExt;
        return this;
    }

    public ObjectFileBuilder setFileSize(final Integer fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public ObjectFileBuilder setMd5(final String md5) {
        this.md5 = md5;
        return this;
    }

    public ObjectFileBuilder setSha256(final String sha256) {
        this.sha256 = sha256;
        return this;
    }

    public ObjectFileBuilder setHydraPublishId(final Integer hydraPublishId) {
        this.hydraPublishId = hydraPublishId;
        return this;
    }

    public ObjectFileBuilder setStatus(final String status) {
        this.status = status;
        return this;
    }

    public ObjectFileBuilder setThumbnail(final byte[] bytes) {
        this.thumbnail = bytes;
        return this;
    }

    public ObjectFile createObjectFile() {
        final ObjectFile objectFile = new ObjectFile();
        objectFile.setDataId(dataId);
        objectFile.setDate(date);
        objectFile.setUserId(userId);
        objectFile.setOid(oid);
        objectFile.setFileLabel(fileLabel);
        objectFile.setFileName(fileName);
        objectFile.setFilePath(filePath);
        objectFile.setFileExt(fileExt);
        //objectFile.setFileSize(fileSize);
        objectFile.setMd5(md5);
        //objectFile.setSha256(sha256);
        //objectFile.setHydraPublishId(hydraPublishId);
        //objectFile.setStatus(status);
        objectFile.setThumbnail(thumbnail);
        return objectFile;
    }
}