
package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class UserProjectFieldView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private UserProjectFieldDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


