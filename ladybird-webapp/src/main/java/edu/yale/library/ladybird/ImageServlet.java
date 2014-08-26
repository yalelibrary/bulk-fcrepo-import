package edu.yale.library.ladybird;

import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(ImageServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oidStr = request.getPathInfo().substring(1);

        if (oidStr.isEmpty()) {
            return;
        }

        int oid = Integer.parseInt(oidStr);

        final ObjectFile objectFile = getObjectFile(oid);

        final byte[] thumb = objectFile.getThumbnail();

        if (thumb == null || thumb.length == 0) {
            logger.error("Null thumb bytes for oid={}", oid);
        }

        final String filename = objectFile.getFileName();
        response.setContentType(getServletContext().getMimeType(filename));
        response.setContentLength(thumb.length);
        response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new ByteArrayInputStream(thumb));
            output = new BufferedOutputStream(response.getOutputStream());
            byte[] buffer = new byte[8192];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.error("Error closing stream for oid={}", oid);
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("Error closing stream for oid={}", oid);
                }
            }
        }
    }

    public ObjectFile getObjectFile(int thumbId) {
        ObjectFileDAO objectFileDAO = new ObjectFileHibernateDAO();
        ObjectFile objectFile = objectFileDAO.findByOid(thumbId);
        return objectFile;
    }


}