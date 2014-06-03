package edu.yale.library.ladybird.web.view;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import java.io.InputStream;

@ManagedBean
public class ImageBean {

    private Logger logger = LoggerFactory.getLogger(ImageBean.class);

    private StreamedContent image;

    public ImageBean() {
        InputStream stream = null;
        try {
            stream = this.getClass().getResourceAsStream("212246c.jpg");
        } catch (Exception e) {
            logger.error("Error dislaying image={}", e);
        }
        image = new DefaultStreamedContent(stream, "image/jpeg");

        if (image == null) {
            logger.debug("Image null");

        }
    }

    public StreamedContent getImage() {
        return this.image;
    }
}