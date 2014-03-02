package edu.yale.library.view;

import edu.yale.library.dao.ObjectLongstringDAO;

import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectLongstringView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private ObjectLongstringDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


