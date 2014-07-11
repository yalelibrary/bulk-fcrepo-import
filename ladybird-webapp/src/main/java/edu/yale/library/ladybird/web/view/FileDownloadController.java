package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.ImportJob;
import edu.yale.library.ladybird.persistence.dao.hibernate.ImportJobHibernateDAO;
import org.apache.commons.io.FileUtils;
import org.omnifaces.util.Faces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class FileDownloadController {

    private Logger logger = getLogger(this.getClass());

    private StreamedContent file;

    public FileDownloadController() {
    }

    @PostConstruct
    public void init() {
        final int importJobId;
        file = null;

        try {
            if (Faces.getRequestParameter("id") == null) {
                logger.error("Null import job id for download.");
                return;
            } else {
                importJobId = Integer.parseInt(Faces.getRequestParameter("id"));
            }

            logger.trace("Looking for import id={}", importJobId);

            final ImportJob importJob = new ImportJobHibernateDAO().findByJobId(importJobId).get(0);

            String fileDir = importJob.getExportJobDir();

            String fileName = importJob.getJobFile();

            if (fileName.isEmpty() || fileDir.isEmpty()) {
                logger.error("Error reading file for download. File name or dir null");
                return;
            }

            byte[] filebytes = FileUtils.readFileToByteArray(new File(fileDir));
            InputStream stream = new ByteArrayInputStream(filebytes);
            file = new DefaultStreamedContent(stream, "text/plain", fileName);
        } catch (Throwable e) {
            logger.error("Error reading or processing file for download", e);
        }
    }

    public StreamedContent getFile() {
        return file;
    }

}
