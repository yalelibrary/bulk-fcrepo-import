package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.file.ImageMagickProcessor;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.engine.model.FunctionConstants;
import edu.yale.library.ladybird.entity.ImportFile;
import edu.yale.library.ladybird.entity.ImportFileBuilder;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectFileBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportFileHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.slf4j.LoggerFactory.getLogger;

public class MediaFunctionProcessor {

    private Logger logger = getLogger(this.getClass());

    private String rootPath = "";

    private String projectDir = "";

    private static String defaultImage = "no-image-found.jpg";

    final ImportFileDAO importFileDAO = new ImportFileHibernateDAO();

    final ImageMagickProcessor imgMagick = new ImageMagickProcessor();

    final ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();

    /**
     * Process images and writes to import file and object file
     *
     * @param importEntityValue import EntityValue
     * @throws IOException if any single conversion fails
     */
    @SuppressWarnings("unchecked")
    public void process(final int importId, final ImportEntityValue importEntityValue) throws IOException {
        logger.debug("Running process");
        final int userId = 1; //TODO check this

        List<Row> rowList = importEntityValue.getContentRows();

        logger.debug("importId={} row list size={}", importId, rowList.size());

        for (int i = 0; i < rowList.size(); i++) {
            final Column<String> f3 = importEntityValue.getRowFieldColumn(FunctionConstants.F3, i);

            checkState(f3.getField().getName().equals(FunctionConstants.F3.getName()), "Found wrong F3 col");

            final String f3Col = f3.getValue();

            logger.debug("Evaluating f3={}", f3Col);

            final Column<String> f1 = importEntityValue.getRowFieldColumn(FunctionConstants.F1, i);

            checkState(f1.getField().getName().equals(FunctionConstants.F1.getName()), "Found wrong F1 col");

            final String oidCol = f1.getValue();
            final Integer oid = Integer.parseInt(oidCol);

            // Update import file and convert image (plus a thumbnail)
            // or, if no image found: update dao with blank image found on project folder & skip ImageMagick step.
            try {

                final File file = new File(getPath(f3Col));

                logger.debug("Eval file={}", file.getAbsolutePath());

                if (file.exists() && !file.isDirectory()) {
                    ImportFile imFile = getBuilder().importId(importId).fileLocation(f3Col).oid(oid).create();
                    importFileDAO.save(imFile);

                    String from = MediaFormat.TIFF.toString();
                    String to = MediaFormat.JPEG.toString();
                    final String outputFilePath = asFormat(getOutPath(f3Col), from, to);

                    byte[] thumbnailBytes = convertImage(f3Col, outputFilePath, MediaFormat.TIFF, MediaFormat.JPEG, oid);

                    ObjectFile exObjectFile = objectFileDAO.findByOid(oid);

                    if (exObjectFile == null) {
                        final ObjectFile objectFile = new ObjectFileBuilder().setDate(new Date()).setFilePath(outputFilePath)
                                .setFileExt(MediaFormat.JPEG.toString()).setOid(oid).setUserId(userId).setFileLabel(f3Col)
                                .setThumbnail(thumbnailBytes).setFileName(f3Col.replace(from, to)).createObjectFile();

                        objectFileDAO.save(objectFile);

                        logger.debug("Saved entity={}", objectFile);

                    } else {
                        exObjectFile.setFileName(f3Col.replace(from, to));
                        exObjectFile.setFileExt(MediaFormat.JPEG.toString());
                        exObjectFile.setFilePath(outputFilePath);
                        exObjectFile.setDate(new Date());
                        exObjectFile.setThumbnail(thumbnailBytes);
                        exObjectFile.setFileLabel(f3Col);

                        objectFileDAO.saveOrUpdateItem(exObjectFile);

                        logger.debug("Updated entity={}", exObjectFile);
                    }
                } else {
                    ImportFile imFile = getBuilder().importId(importId).oid(oid).fileLocation(getPath(defaultImage)).create();
                    importFileDAO.save(imFile);

                    String fileName = "N/A";

                    byte[] thumbnail = convertImage(oid);

                    ObjectFile objectFile = new ObjectFileBuilder().setDate(new Date()).setFilePath(getPath(defaultImage))
                            .setFileExt(MediaFormat.JPEG.toString()).setOid(oid).setUserId(userId).setFileLabel(fileName)
                            .setThumbnail(thumbnail).setFileName(fileName).createObjectFile();

                    logger.trace("Saving entity={}", objectFile);

                    objectFileDAO.save(objectFile);
                }
            } catch (IOException e) {
                logger.error("Error/warning persisting entity", e);
                throw e;
            }
        }
    }

    /**
     * Process images and writes to import file and object file
     *
     * @param importEntityValue import EntityValue
     * @throws IOException if any single conversion fails
     */
    @SuppressWarnings("unchecked")
    public void processWithoutImage(final int importId, final ImportEntityValue importEntityValue) throws IOException {
        final int userId = 1; //TODO check this

        List<Row> rowList = importEntityValue.getContentRows();

        for (int i = 0; i < rowList.size(); i++) {
            final Column<String> f3 = importEntityValue.getRowFieldColumn(FunctionConstants.F3, i);

            checkState(f3.getField().getName().equals(FunctionConstants.F3.getName()), "Found wrong F3 col");

            final String f3Col = f3.getValue();

            logger.debug("Evaluating f3={}", f3Col);

            final Column<String> f1 = importEntityValue.getRowFieldColumn(FunctionConstants.F1, i);

            checkState(f1.getField().getName().equals(FunctionConstants.F1.getName()), "Found wrong F1 col");

            final String oidCol = f1.getValue();
            final Integer oid = Integer.parseInt(oidCol);

            // Update import file and convert image (plus a thumbnail)
            // or, if no image found: update dao with blank image found on project folder & skip ImageMagick step.
            try {
                ImportFile imFile = getBuilder().importId(importId).oid(oid).fileLocation(getPath(defaultImage)).create();
                importFileDAO.save(imFile);

                String fileName = "N/A";

                byte[] thumbnail = convertImage(oid);

                ObjectFile objectFile = new ObjectFileBuilder().setDate(new Date()).setFilePath(getPath(defaultImage))
                        .setFileExt(MediaFormat.JPEG.toString()).setOid(oid).setUserId(userId).setFileLabel(fileName)
                        .setThumbnail(thumbnail).setFileName(fileName).createObjectFile();

                logger.trace("Saving entity={}", objectFile);

                objectFileDAO.save(objectFile);
            } catch (IOException e) {
                logger.error("Error/warning persisting entity", e);
                throw e;
            }
        }
    }

    /**
     * Converts image and updates object file table
     *
     * @param fileName filename
     * @param fromExt  ext to convert to
     * @param toExt    ext to convert to
     * @param oid      oid
     */
    private byte[] convertImage(final String fileName, final String outputFilePath, final MediaFormat fromExt, final MediaFormat toExt, int oid)
            throws IOException {
        // 1. convert
        final String inputFilePath = getPath(fileName);
        try {
            imgMagick.toFormat(inputFilePath, outputFilePath);

            logger.trace("Converted image to={}", outputFilePath);
        } catch (Exception e) {
            logger.error("Error or warning converting image={}", e.getMessage()); //N.B. ignore errors and warnings
            logger.trace("Error", e);
        }

        //1b. convert to thumbnail
        final String thumbnailPath = asFormat(getOutPath(fileName), fromExt.toString(), MediaFormat.THUMBNAIL.toString());

        try {
            imgMagick.toThumbnailFormat(outputFilePath, thumbnailPath);

            logger.trace("Converted image to thumbnail={}", thumbnailPath);
        } catch (Exception e) {
            logger.error("Error or warning converting to thumbnail={}", e.getMessage()); //N.B. ignore errors and warnings
            logger.trace("Error", e);
        }

        //2. prepare object file and persist. If the thumbnail is not found, try a default. If that fails, throw.
        byte[] thumbnailByes;
        try {
            thumbnailByes = getBytes(thumbnailPath);
        } catch (IOException e) {
            logger.error("Thumbnail image not found for oid={}", oid, e); //this means a conversion error
            try {
                thumbnailByes = getBytes(ImageMagickProcessor.getBlankImagePath());
            } catch (IOException io) {
                logger.error("Error setting default thumbnail image for oid={}", oid, io);
                throw io;
            }
        }

        if (thumbnailByes == null || thumbnailByes.length == 0) {
            throw new IOException("No thumbnail for oid=" + oid);
        }

        return thumbnailByes;
    }

    /**
     * Converts image and updates object file table
     */
    private byte[] convertImage(int oid) throws IOException {
        final String thumbnailPath = ImageMagickProcessor.getBlankImagePath();

        byte[] thumbnail = getBytes(thumbnailPath);

        if (thumbnail == null || thumbnail.length == 0) {
            logger.error("Null bytes for thumbnail for oid={}", oid);
        }

        return thumbnail;
    }


    private byte[] getBytes(String filePath) throws IOException {
        byte[] bytes;
        try {
            bytes = FileUtils.readFileToByteArray(new File(filePath));
        } catch (IOException e) {
            logger.error("Error getting bytes for projectDir={}", projectDir, e);
            throw e;
        }
        return bytes;
    }

    private String getPath(final String fileName) {
        return rootPath + File.separator + projectDir + File.separator + fileName;
    }

    private String getOutPath(final String fileName) {
        return rootPath + File.separator + projectDir + File.separator + "exports" + File.separator + fileName;
    }

    private static String asFormat(final String fileName, final String from, final String ext) {
        return fileName.replace(from, ext);
    }

    private enum MediaFormat {

        TIFF(".tif"),
        JPEG(".jpg"),
        JPEG2000(".jp2"),
        THUMBNAIL(".png");

        String name;

        private MediaFormat(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public MediaFunctionProcessor(String rootPath, String projectDir) {
        this.rootPath = rootPath;
        this.projectDir = projectDir;
    }

    private static ImportFileBuilder getBuilder() {
        return new ImportFileBuilder().date(new Date());
    }
}
