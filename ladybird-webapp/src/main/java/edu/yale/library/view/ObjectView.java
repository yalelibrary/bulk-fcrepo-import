package edu.yale.library.view;

import edu.yale.library.dao.ObjectDAO;

import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private ObjectDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


