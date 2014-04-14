package edu.yale.library.ladybird.persistence.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T, ID extends Serializable> {

    /** Get only a number of rows */
    List<T> find(int rowNum, int count);

    List<T> findAll();

    Integer save(T entity);

    void saveOrUpdateList(List<T> itemList);


}
