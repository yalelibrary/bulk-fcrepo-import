package edu.yale.library.ladybird.web;


public class CronBean {

    private String importCronExpression = "0/10 * * * * ?";

    private String exportCronExpression = "0/10 * * * * ?";

    private String filePickerCronExpression;

    private String fileNotificationCronExpression = "0/10 * * * * ?";

    private String imageConversionExpression = "0/10 * * * * ?";

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

    public String getImageConversionExpression() {
        return imageConversionExpression;
    }

    public void setImageConversionExpression(String imageConversionExpression) {
        this.imageConversionExpression = imageConversionExpression;
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
