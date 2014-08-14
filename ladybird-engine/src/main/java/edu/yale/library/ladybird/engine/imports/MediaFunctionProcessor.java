package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.file.ImageMagickProcessor;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.entity.ImportFile;
import edu.yale.library.ladybird.entity.ImportFileBuilder;
import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.ObjectFileBuilder;
import edu.yale.library.ladybird.persistence.dao.ImportFileDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportFileHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

public class MediaFunctionProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /** root path */
    private String rootPath = "";

    /** relative path */
    private String path = "";

    @SuppressWarnings("unchecked")
    public void process(final List<ImportEntity.Row> rowList, final int MEDIA_COLUMN) {
        logger.debug("Processing list size={}", rowList.size());

        for (Row row: rowList) {
            final List<ImportEntity.Column> columnsList = row.getColumns();
            final Column<String> c = columnsList.get(MEDIA_COLUMN);
            convertImage(c.getValue(), MediaFormat.TIFF, MediaFormat.JPEG);
        }
    }

    /**
     * Process images and writes to import file and object file
     * @param rowList
     * @param MEDIA_COLUMN
     * @param OID_COLUMN
     */
    @SuppressWarnings("unchecked")
    public void process(final int importId, final List<ImportEntity.Row> rowList, final int MEDIA_COLUMN, final int OID_COLUMN) {

        final ImportFileDAO importFileDAO = new ImportFileHibernateDAO(); //TODO

        logger.debug("Processing with F1 col num={}, F3 col num={}, list size={}", OID_COLUMN, MEDIA_COLUMN, rowList.size());

        for (Row row: rowList) {
            final List<ImportEntity.Column> columnsList = row.getColumns();
            final Column<String> c = columnsList.get(MEDIA_COLUMN);

            try {
                //1. Update import file

                final Column<String> o = columnsList.get(OID_COLUMN);
                Integer oid = Integer.parseInt(o.getValue());

                logger.debug("Considering file location={}", c.getValue());

                final File f = new File(getPath(c.getValue()));

                if (f.exists()) {

                ImportFile importFile = new ImportFileBuilder().setImportId(importId)
                        .setFileLocation(c.getValue()).setDate(new Date()).setOid(oid).createImportFile();

                importFileDAO.save(importFile);

                logger.trace("Saved entity={}", importFile.toString());

                //2. convert image
                convertImage(c.getValue(), MediaFormat.TIFF, MediaFormat.JPEG, oid);
                } else { //no image found. update dao with blank image found on project folder & skip image magick. TODO
                    logger.debug("Replacing no image with path={}", getPath("no-image-found.jpg"));
                    ImportFile importFile = new ImportFileBuilder().setImportId(importId)
                            .setFileLocation(getPath("no-image-found.jpg")).setDate(new Date()).setOid(oid).createImportFile();
                    importFileDAO.save(importFile);

                    convertImage("N/A", getPath("no-image-found.jpg"), oid);

                    logger.trace("Saved entity={}", importFile.toString());
                }
            } catch (Exception e) {
                logger.debug("Error/warning persisting entity", e); //ignore persistence error
            }
        }
    }

    @Deprecated
    private void convertImage(final String fileName, final MediaFormat fromExt, final MediaFormat toExt) {
        final ImageMagickProcessor imgMagick = new ImageMagickProcessor();
        try {
            final String inputFilePath = getPath(fileName);
            final String outputFilePath = asFormat(getOutPath(fileName), fromExt.toString(), toExt.toString());
            imgMagick.toFormat(inputFilePath, outputFilePath);
            logger.trace("Converted image to={}", outputFilePath);
        } catch (Exception e) {
            logger.error("Error or warning converting image", e.getMessage()); //ignore errors and warnings
        }
    }


    /**
     * Converts image and updates object file table
     * @param fileName
     * @param fromExt
     * @param toExt
     * @param oid
     */
    private void convertImage(final String fileName, final MediaFormat fromExt, final MediaFormat toExt, int oid) {
        final ImageMagickProcessor imgMagick = new ImageMagickProcessor();

        // 1. convert

        final String inputFilePath = getPath(fileName);
        final String outputFilePath = asFormat(getOutPath(fileName), fromExt.toString(), toExt.toString());
        try {

            imgMagick.toFormat(inputFilePath, outputFilePath);
            logger.debug("Converted image to={}", outputFilePath);
        } catch (Exception e) {
            logger.error("Error or warning converting image", e.getMessage()); //ignore errors and warnings
            logger.trace("Error={}", e);
        }

        //FIXME exception order (if error occurs, object file is still writtten)

        //2. prepare object file and persist
        ObjectFile objectFile = new ObjectFileBuilder()
                .setDate(new Date()).setFilePath(outputFilePath)
                .setFileExt(MediaFormat.JPEG.toString())
                .setOid(oid).setUserId(1).setFileLabel(fileName)
                .setFileName(fileName.replace(fromExt.toString(), toExt.toString())).createObjectFile(); //FIXME userid

        logger.trace("Saving entity={}", objectFile);

        ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();
        objectFileDAO.save(objectFile);
        logger.trace("Saved.");
    }

    /**
     * Converts image and updates object file table
     * @param fileName
     * @param oid
     */
    private void convertImage(final String fileName, final String outputFilePath, int oid) {

        //2. prepare object file and persist
        ObjectFile objectFile = new ObjectFileBuilder()
                .setDate(new Date()).setFilePath(outputFilePath)
                .setFileExt(MediaFormat.JPEG.toString())
                .setOid(oid).setUserId(1).setFileLabel(fileName)
                .setFileName("N/A").createObjectFile(); //FIXME userid

        logger.trace("Saving entity={}", objectFile);

        ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();
        objectFileDAO.save(objectFile);
        logger.trace("Saved.");
    }

    private String getPath(final String fileName) {
        logger.trace("Src path={}", rootPath + File.separator + path + File.separator + fileName);
        return rootPath + File.separator + path + File.separator + fileName;
    }

    private String getOutPath(final String fileName) {
        logger.trace("Dest path value={}", rootPath + File.separator + path + File.separator + "exports" + File.separator + fileName);
        return rootPath + File.separator + path + File.separator + "exports" + File.separator + fileName;
    }

    private static String asFormat(final String fileName, final String from, final String ext) {
        return fileName.replace(from, ext);
    }

    private enum MediaFormat {
        TIFF(".tif"),
        JPEG(".jpg"),
        JPEG2000(".jp2");

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
