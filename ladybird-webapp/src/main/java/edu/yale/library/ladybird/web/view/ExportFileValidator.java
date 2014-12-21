package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.persistence.dao.SettingsDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.SettingsHibernateDAO;
import org.slf4j.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.getProperty;
import static org.slf4j.LoggerFactory.getLogger;

@FacesValidator("edu.yale.library.ladybird.web.view.ExportFileValidator")
public class ExportFileValidator implements Validator {

    private Logger logger = getLogger(ExportFileValidator.class);

    private SettingsDAO dao = new SettingsHibernateDAO(); //TODO

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) {
        try {
            String rootPath = dao.findByProperty("import_root_path").getValue();
            String userDir = o.toString();
            Path path = Paths.get(rootPath + getProperty("file.separator") + userDir);

            if (!Files.isDirectory(path)) {
                throw new ValidatorException(getException());
            }
        } catch (ValidatorException e) {
            logger.error("Error validating export file directory input", e);
            throw e;
        }
    }

    private FacesMessage getException() {
        FacesMessage msg = new FacesMessage("Invalid directory.");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        return msg;
    }

}
