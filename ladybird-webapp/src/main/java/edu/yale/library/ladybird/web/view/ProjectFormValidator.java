package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.persistence.dao.ProjectDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ProjectHibernateDAO;
import org.hibernate.HibernateException;
import org.slf4j.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Validator for all relevant inputs on the page
 */
@FacesValidator("edu.yale.library.ladybird.web.view.ProjectFormValidator")
public class ProjectFormValidator implements Validator {

    private Logger logger = getLogger(this.getClass());

    private ProjectDAO dao = new ProjectHibernateDAO(); //TODO

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) {
        final FacesMessage msg = new FacesMessage("Specify non-existing project label");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);

        try {
            if (uiComponent.getId().equals("projectLabelInput")) {
                List<Project> projectsByLabel = dao.findByLabel(o.toString());

                if (!(projectsByLabel == null || projectsByLabel.isEmpty())) {
                    throw new ValidatorException(msg);
                }
            } else {
                logger.trace("Skipping validationf for component={}", uiComponent.getId());
            }
        } catch (HibernateException e) {
            throw e;
        }
    }

}
