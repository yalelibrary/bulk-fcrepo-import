package edu.yale.library.ladybird.engine.file;

import com.google.common.base.Preconditions;
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

public class ImageMagickProcessor implements ImageProcessor {

    private static Logger logger = LoggerFactory.getLogger(ImageMagickProcessor.class);

    /** from .properties */
    private static final String IMJ_CMD_PATH = ApplicationProperties.CONFIG_STATE.IMAGE_MAGICK_PATH;

    /** from im4java */
    public static final String IM_4_JAVA_TOOPATH = "IM4JAVA_TOOPATH";

    public void toFormat(final String src, final String dest) {
        String sourceImage;
        String destImage;

        try {
            sourceImage = Preconditions.checkNotNull(src);
            destImage = Preconditions.checkNotNull(dest);
        } catch (NullPointerException e) {
            return; //ignore
        }

        final ConvertCmd cmd = new ConvertCmd();

        String imageMagickPath = getImgMagickPath();

        logger.trace("Image magick path={}", imageMagickPath);

        if (!imageMagickPath.isEmpty()) {
            cmd.setSearchPath(imageMagickPath);
        } else {
            logger.warn("ImageMagick program path property not found in .properties, db, or as IM4JAVA_TOOLPATH. "
                    + "Assuming it exists on path anyway");
        }

        try {
            logger.trace("Converting file={}", src);
            final IMOperation op = new IMOperation();
            op.addImage(sourceImage);
            op.addImage(destImage);
            cmd.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    public void toThumbnailFormat(final String src, final String dest) {
        String sourceImage;
        String destImage;

        try {
            sourceImage = Preconditions.checkNotNull(src);
            destImage = Preconditions.checkNotNull(dest);
        } catch (NullPointerException e) {
            return; //ignore
        }

        final ConvertCmd cmd = new ConvertCmd();

        String imageMagickPath = getImgMagickPath();

        if (!imageMagickPath.isEmpty()) {
            cmd.setSearchPath(imageMagickPath);
        } else {
            logger.warn("ImageMagick program path property not found in .properties, db, or as IM4JAVA_TOOLPATH. "
                    + "Assuming it exists on path anyway");
        }

        try {
            logger.trace("Converting file={}", src);
            final IMOperation op = new IMOperation();
            op.addImage(sourceImage);
            op.thumbnail(150, 150);
            op.addImage(destImage);
            cmd.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new RuntimeException("Error processing image", e);
        }
    }

    /**
     * Get image magick path
     * @return path or empty string since the field is held statically for now
     */
    public static String getImgMagickPath() {
        try {
            final SettingsDAO settingsDAO = new SettingsHibernateDAO();
            final Settings s = settingsDAO.findByProperty(ApplicationProperties.IMAGE_MAGICK_PATH_ID);
            String value = s.getValue();

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

    public static String getBlankImagePath() {
        try {
            final SettingsDAO settingsDAO = new SettingsHibernateDAO();
            final Settings s = settingsDAO.findByProperty(ApplicationProperties.NO_IMAGE_FOUND_PATH);
            String value = s.getValue();
            return value;
        } catch (Exception e) {
            logger.error("Error getting blank image path", e);
            throw new NullPointerException("No image found");
        }
    }

    public static boolean pathContains(final String path) {
        final String PROGRAM = "convert";
        File file = new File(path + File.separator + PROGRAM);
        return file.exists();
    }

}
