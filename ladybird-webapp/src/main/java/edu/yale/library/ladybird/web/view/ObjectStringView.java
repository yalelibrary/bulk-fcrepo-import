package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.persistence.dao.ObjectStringDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class ObjectStringView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private ObjectStringDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


