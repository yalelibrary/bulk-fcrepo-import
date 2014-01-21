package edu.yale.library.view;

import edu.yale.library.dao.GenericDAO;

public class AbstractView<T>
{
    protected GenericDAO<T, Integer> dao;

    public AbstractView()
    {
    }

    public String find()
    {
        return dao.findAll().toString();
    }

    public void initFields()
    {
        DaoInitializer.injectFields(this);
    }
}