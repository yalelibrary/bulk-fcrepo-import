package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.kernel.dao.GenericDAO;

public class AbstractView<T> {
    protected GenericDAO<T, Integer> dao;

    public AbstractView() {
    }

    public String find() {
        return dao.findAll().toString();
    }

    public void initFields() {
        DaoInitializer.injectFields(this);
    }
}