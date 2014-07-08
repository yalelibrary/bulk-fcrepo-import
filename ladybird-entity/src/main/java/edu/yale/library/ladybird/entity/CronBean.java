package edu.yale.library.ladybird.entity;

/**
 *
 */
public class CronBean {

    private String importCronExpression;

    private String exportCronExpression;

    private String filePickerCronExpression;

    private String fileNotificationCronExpression;

    public String getImportCronExpression() {
        return importCronExpression;
    }

    public void setImportCronExpression(String importCronExpression) {
        this.importCronExpression = importCronExpression;
    }

    public String getExportCronExpression() {
        return exportCronExpression;
    }

    public void setExportCronExpression(String exportCronExpression) {
        this.exportCronExpression = exportCronExpression;
    }

    public String getFilePickerCronExpression() {
        return filePickerCronExpression;
    }

    public void setFilePickerCronExpression(String filePickerCronExpression) {
        this.filePickerCronExpression = filePickerCronExpression;
    }

    public String getFileNotificationCronExpression() {
        return fileNotificationCronExpression;
    }

    public void setFileNotificationCronExpression(String fileNotificationCronExpression) {
        this.fileNotificationCronExpression = fileNotificationCronExpression;
    }

    @Override
    public String toString() {
        return "CronBean{"
                + "importCronExpression='" + importCronExpression + '\''
                + ", exportCronExpression='" + exportCronExpression + '\''
                + ", filePickerCronExpression='" + filePickerCronExpression + '\''
                + '}';
    }
}
