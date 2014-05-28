package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.persistence.dao.GenericDAO;

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

    public String ok() {
        return NavigationCase.OK.toString();
    }

    public String fail() {
        return NavigationCase.FAIL.toString();
    }
}