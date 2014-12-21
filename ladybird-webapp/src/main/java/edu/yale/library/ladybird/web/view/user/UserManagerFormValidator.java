package edu.yale.library.ladybird.web.view.user;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserHibernateDAO;
import org.hibernate.HibernateException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.List;

/**
 * Validator for all relevant inputs on the page
 */
@FacesValidator("edu.yale.library.ladybird.web.view.user.UserManagerFormValidator")
public class UserManagerFormValidator implements Validator {

    private UserDAO dao = new UserHibernateDAO(); //TODO

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) {
        FacesMessage msg = new FacesMessage("Specify non-existing username or email");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);

        try {
            List<User> userByName = dao.findByUsername(o.toString());

            if (!(userByName == null || userByName.isEmpty())) {
                throw new ValidatorException(msg);
            }

            List<User> userByEmail = dao.findByEmail(o.toString());

            if (!(userByEmail == null || userByEmail.isEmpty())) {
                throw new ValidatorException(msg);
            }
        } catch (HibernateException e) {
            throw e;
        }
    }

}
