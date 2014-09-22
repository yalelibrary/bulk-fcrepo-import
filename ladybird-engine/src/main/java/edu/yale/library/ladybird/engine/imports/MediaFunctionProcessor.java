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
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class MediaFunctionProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * root path
     */
    private String rootPath = "";

    /**
     * relative path
     */
    private String path = "";

    final ImportFileDAO importFileDAO = new ImportFileHibernateDAO(); //TODO

    final ImageMagickProcessor imgMagick = new ImageMagickProcessor(); //TODO

    final ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO(); //TODO

    @SuppressWarnings("unchecked")
    @Deprecated
    public void process(final List<ImportEntity.Row> rowList, final int MEDIA_COLUMN) {
        logger.debug("Processing list size={}", rowList.size());

        for (Row row : rowList) {
            final List<ImportEntity.Column> columnsList = row.getColumns();
            final Column<String> c = columnsList.get(MEDIA_COLUMN);
            convertImage(c.getValue(), MediaFormat.TIFF, MediaFormat.JPEG);
        }
    }

    /**
     * Process images and writes to import file and object file
     * @param importEntityValue import EntityValue
     * @throws IOException if any single conversion fails
     */
    @SuppressWarnings("unchecked")
    public void process(final int importId, final ImportEntityValue importEntityValue) throws IOException {
//        logger.trace("Processing with F1 col num={}, F3 col num={}, list size={}", OID_COLUMN, MEDIA_COLUMN, rowList.size());

        final int userId = 1; //TODO check this

        List<Row> rowList = importEntityValue.getContentRows();

        for (int i = 0 ; i < rowList.size(); i++) {
            //final List<ImportEntity.Column> columnsList = rowList.get(i).getColumns();

            final Column<String> f3 = importEntityValue.getRowFieldColumn(FunctionConstants.F3, i);
            checkState(f3.getField().getName().equals(FunctionConstants.F3.getName()), "Found wrong F3 col");

            final String f3Col = f3.getValue();


            final Column<String> f1 = importEntityValue.getRowFieldColumn(FunctionConstants.F1, i);
            checkState(f1.getField().getName().equals(FunctionConstants.F1.getName()), "Found wrong F1 col");

            final String oidCol = f1.getValue();

            final Integer oid = Integer.parseInt(oidCol);

            try {
                //1. Update import file
                logger.debug("Eval file={}", f3Col);

                final File file = new File(getPath(f3Col));

                if (file.exists() && !file.isDirectory()) {
                    ImportFile importFile = new ImportFileBuilder().setImportId(importId)
                            .setFileLocation(f3Col).setDate(new Date()).setOid(oid).createImportFile();

                    importFileDAO.save(importFile);

                    //2. convert image and add a thumbnail
                    convertImage(f3Col, MediaFormat.TIFF, MediaFormat.JPEG, oid, userId);
                } else { //no image found. update dao with blank image found on project folder & skip image magick. no thumbnail.
                    ImportFile importFile = new ImportFileBuilder().setImportId(importId).setOid(oid)
                            .setFileLocation(getPath("no-image-found.jpg")).setDate(new Date()).createImportFile();
                    importFileDAO.save(importFile);

                    convertImage("N/A", getPath("no-image-found.jpg"), oid, userId);
                }
            } catch (IOException e) {
                logger.error("Error/warning persisting entity", e);
                throw e;
            }
        }
    }

    @Deprecated
    private void convertImage(final String fileName, final MediaFormat fromExt, final MediaFormat toExt) {
        logger.error("Called deprecated method");
        try {
            final String inputFilePath = getPath(fileName);
            final String outputFilePath = asFormat(getOutPath(fileName), fromExt.toString(), toExt.toString());
            imgMagick.toFormat(inputFilePath, outputFilePath);

            logger.trace("Converted image to={}", outputFilePath);
        } catch (Exception e) {
            logger.debug("Error or warning converting image={}", e.getMessage()); //N.B. ignore errors and warnings
            logger.error("Error", e);
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
    private void convertImage(final String fileName, final MediaFormat fromExt, final MediaFormat toExt, int oid, int userId)
            throws IOException {
        // 1. convert
        final String inputFilePath = getPath(fileName);
        final String outputFilePath = asFormat(getOutPath(fileName), fromExt.toString(), toExt.toString());
        try {
            imgMagick.toFormat(inputFilePath, outputFilePath);

            logger.trace("Converted image to={}", outputFilePath);
        } catch (Exception e) {
            logger.error("Error or warning converting image={}", e.getMessage()); //N.B. ignore errors and warnings
            logger.trace("Error", e);
        }

        //1b. get thumbnail //TODO write to tmp
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
            } catch (IOException e1) {
                logger.error("Error setting default thumbnail image for oid={}", oid, e1);
                throw e1;
            }
        }

        if (thumbnailByes == null || thumbnailByes.length == 0) {
            throw new IOException("No thumbnail for oid=" + oid);
        }

        final ObjectFile objectFile = new ObjectFileBuilder().setDate(new Date()).setFilePath(outputFilePath)
                .setFileExt(MediaFormat.JPEG.toString()).setOid(oid).setUserId(userId).setFileLabel(fileName)
                .setThumbnail(thumbnailByes)
                .setFileName(fileName.replace(fromExt.toString(), toExt.toString())).createObjectFile(); //FIXME userid

        objectFileDAO.save(objectFile);

        logger.trace("Saved entity{}", objectFile.toString());
    }

    /**
     * Converts image and updates object file table
     */
    private void convertImage(final String fileName, final String outputFilePath, int oid, int userId) throws IOException {
        final String thumbnailPath = ImageMagickProcessor.getBlankImagePath();

        byte[] thumbnail = getBytes(thumbnailPath);

        if (thumbnail == null || thumbnail.length == 0) {
            logger.error("Null bytes for thumbnail for oid={}", oid);
        }

        ObjectFile objectFile = new ObjectFileBuilder()
                .setDate(new Date()).setFilePath(outputFilePath)
                .setFileExt(MediaFormat.JPEG.toString())
                .setOid(oid).setUserId(userId).setFileLabel(fileName)
                .setThumbnail(thumbnail)
                .setFileName("N/A").createObjectFile();

        logger.trace("Saving entity={}", objectFile);

        objectFileDAO.save(objectFile);

        logger.trace("Saved.");
    }

    private byte[] getBytes(String filePath) throws IOException {
        byte[] bytes;
        try {
            bytes = FileUtils.readFileToByteArray(new File(filePath));
        } catch (IOException e) {
            logger.error("Error getting bytes for path={}", path, e);
            throw e;
        }
        return bytes;
    }

    private String getPath(final String fileName) {
        return rootPath + File.separator + path + File.separator + fileName;
    }

    private String getOutPath(final String fileName) {
        return rootPath + File.separator + path + File.separator + "exports" + File.separator + fileName;
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

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public MediaFunctionProcessor(String rootPath, String path) {
        this.rootPath = rootPath;
        this.path = path;
    }
}
