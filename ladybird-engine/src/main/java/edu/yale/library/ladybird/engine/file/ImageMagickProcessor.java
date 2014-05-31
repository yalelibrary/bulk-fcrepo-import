package edu.yale.library.ladybird.engine.file;

import edu.yale.library.ladybird.kernel.ApplicationProperties;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ImageMagickProcessor implements ImageProcessor {

    private Logger logger = LoggerFactory.getLogger(ImageMagickProcessor.class);

    private static final String IMJ_CMD_PATH = ApplicationProperties.CONFIG_STATE.IMAGE_MAGICK_PATH; //TODO from DB?

    public void toFormat(final String src, final String dest) {
        //logger.debug("ImageMagick command path={}", IMJ_CMD_PATH);
        logger.debug("Converting file={}", src);

        try {
            final ConvertCmd cmd = new ConvertCmd();
            cmd.setSearchPath(IMJ_CMD_PATH);

            final IMOperation op = new IMOperation();
            op.addImage(src);
            op.addImage(dest);

            cmd.run(op);
        } catch (IOException | InterruptedException | IM4JavaException e) {
            throw new ImageProcessingException(e);
        }
    }

}
