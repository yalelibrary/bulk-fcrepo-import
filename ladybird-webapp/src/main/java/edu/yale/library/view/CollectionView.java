package edu.yale.library.view;

import edu.yale.library.dao.CollectionDAO;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
@SuppressWarnings("unchecked")
public class CollectionView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    @Inject
    private CollectionDAO entityDAO;

    @PostConstruct
    public void init() {
        initFields();
        dao = entityDAO;
    }

}


