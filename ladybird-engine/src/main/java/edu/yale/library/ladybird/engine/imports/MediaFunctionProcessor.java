package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.engine.file.ImageMagickProcessor;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.yale.library.ladybird.engine.imports.ImportEntity.Row;
import edu.yale.library.ladybird.engine.imports.ImportEntity.Column;

import java.util.List;

public class MediaFunctionProcessor {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Path where images are read and written to. */
    private static String imageRootPath = ApplicationProperties.CONFIG_STATE.IMAGE_ROOT_PATH; //TODO db

    @SuppressWarnings("unchecked")
    public void process(final List<ImportEntity.Row> rowList, final int MEDIA_COLUMN) {
        logger.debug("Processing list size={}", rowList.size());

        for (Row row: rowList) {
            final List<ImportEntity.Column> columnsList = row.getColumns();
            final Column<String> c = columnsList.get(MEDIA_COLUMN);
            convertImage(c.getValue(), MediaFormat.TIFF, MediaFormat.JPEG);
        }
    }

    private void convertImage(final String fileName, final MediaFormat fromExt, final MediaFormat toExt) {
        final ImageMagickProcessor imgMagick = new ImageMagickProcessor();
        try {
            final String inputFilePath = getPath(fileName);
            final String outputFilePath = asFormat(getPath(fileName), fromExt.toString(), toExt.toString());
            imgMagick.toFormat(inputFilePath, outputFilePath);
            logger.debug("Converted image to={}", outputFilePath);
        } catch (Exception e) {
            logger.error("Error converting image", e);
        }
    }

    private static String getPath(final String fileName) {
        return imageRootPath + System.getProperty("file.separator") + fileName;
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
}
