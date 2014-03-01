
package edu.yale.library.view;

import edu.yale.library.dao.ObjectAcidDAO;

import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class ObjectAcidView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private ObjectAcidDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


