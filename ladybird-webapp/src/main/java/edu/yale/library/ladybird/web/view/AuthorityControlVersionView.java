package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.persistence.dao.AuthorityControlVersionDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unchecked")
@ManagedBean
@RequestScoped
public class AuthorityControlVersionView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private AuthorityControlVersionDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


