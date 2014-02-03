package edu.yale.library.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T, ID extends Serializable>
{

    List<T> findAll();

    void save(T entity);


}
