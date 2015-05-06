package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImportFileBuilder {
    private Integer importId;
    private Date date;
    private String fileLocation;
    private int oid;
    private int userId;
    private Integer code;
    private String error;
    private String type;
    private String label;

    public ImportFileBuilder importId(Integer importId) {
        this.importId = importId;
        return this;
    }

    public ImportFileBuilder date(Date date) {
        this.date = date;
        return this;
    }

    public ImportFileBuilder fileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
        return this;
    }

    public ImportFileBuilder oid(int oid) {
        this.oid = oid;
        return this;
    }

    public ImportFileBuilder userId(int userId) {
        this.userId = userId;
        return this;
    }

    public ImportFileBuilder code(Integer code) {
        this.code = code;
        return this;
    }

    public ImportFileBuilder error(String error) {
        this.error = error;
        return this;
    }

    public ImportFileBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ImportFileBuilder label(String label) {
        this.label = label;
        return this;
    }

    public ImportFile create() {
        final ImportFile importFile = new ImportFile();
        importFile.setImportId(importId);
        importFile.setDate(date);
        importFile.setFileLocation(fileLocation);
        importFile.setOid(oid);
        importFile.setUserId(userId);
        importFile.setCode(code);
        importFile.setError(error);
        importFile.setType(type);
        importFile.setLabel(label);
        return importFile;
    }
}