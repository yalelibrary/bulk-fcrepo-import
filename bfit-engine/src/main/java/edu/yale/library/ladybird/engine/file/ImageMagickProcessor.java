package edu.yale.library.ladybird.engine.file;

import edu.yale.library.ladybird.entity.Settings;
import edu.yale.library.ladybird.kernel.ApplicationProperties;
import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ImageMagickProcessor implements ImageProcessor {

    private static Logger logger = LoggerFactory.getLogger(ImageMagickProcessor.class);

    /** Image magick path, built only once */
    private static String IMAGEMAGICK_PATH = getImgMagickPath();

    /** from config .properties */
    private static final String IMJ_CMD_PATH = ApplicationProperties.CONFIG_STATE.IMAGE_MAGICK_PATH;

    /** from im4java */
    public static final String IM_4_JAVA_TOOPATH = "IM4JAVA_TOOPATH";

    private static final int THUMBNAIL_HEIGHT = 150;

    private static final int THUMBNAIL_WIDTH = 150;

    private static final double THUMBNAIL_QUALITY = 10;

    public void toFormat(final String src, final String dst) {
        if (src == null || dst == null) {
            logger.debug("Source or destination image null");
            return;
        }

        final ConvertCmd cmd = buildImageMagickCommand();

        try {
            logger.trace("Converting file={}", src);
            IMOperation op = new IMOperation();
            op.addImage(src);
            op.addImage(dst);
            cmd.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    public void toThumbnailFormat(final String src, final String dst) {
        if (src == null || dst == null) {
            logger.debug("Source or destination image null");
            return;
        }

        final ConvertCmd cmd = buildImageMagickCommand();

        try {
            IMOperation op = new IMOperation();
            op.addImage(src);
            op.thumbnail(THUMBNAIL_HEIGHT, THUMBNAIL_WIDTH);
            op.quality(THUMBNAIL_QUALITY);
            op.strip();
            op.addImage(dst);
            cmd.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Error processing thumbnail", e);
        }
    }

    public static String getBlankImagePath() {
        try {
            final SettingsDAO settingsDAO = new SettingsHibernateDAO();
            final Settings s = settingsDAO.findByProperty(ApplicationProperties.NO_IMAGE_FOUND_PATH);
            return s.getValue();
        } catch (Exception e) {
            logger.error("Error getting blank image path", e);
            throw new NullPointerException("No image found!");
        }
    }

    private ConvertCmd buildImageMagickCommand() {
        final ConvertCmd cmd = new ConvertCmd();
        final String imageMagickPath = IMAGEMAGICK_PATH;

        if (!imageMagickPath.isEmpty()) {
            cmd.setSearchPath(imageMagickPath);
        } else {
            logger.warn("ImageMagick path not found in .properties, db, or as IM4JAVA_TOOLPATH.");
            logger.warn("Assuming ImageMagick exists on path anyway");
        }
        return cmd;
    }


    /**
     * Get image magick path or empty string
     * @return path or empty string since the field is held statically for now
     */
    private static String getImgMagickPath() {
        try {
            final SettingsDAO settingsDAO = new SettingsHibernateDAO();
            final Settings s = settingsDAO.findByProperty(ApplicationProperties.IMAGE_MAGICK_PATH_ID);
            final String value = s.getValue();

            if (!value.isEmpty()) {
                return value;
            } else if (System.getProperty(IM_4_JAVA_TOOPATH) != null) {
                return System.getProperty(IM_4_JAVA_TOOPATH);
            } else if (!IMJ_CMD_PATH.isEmpty() && pathContains(IMJ_CMD_PATH)) {
                return IMJ_CMD_PATH;
            }
        } catch (Exception e) {
            logger.error("Error getting image magick path property", e);
        }
        return "";
    }

    private static boolean pathContains(final String path) {
        final String TOOL = "convert";
        File file = new File(path + File.separator + TOOL);
        return file.exists();
    }

}
