package edu.yale.library.ladybird.persistence.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface GenericDAO<T, ID extends Serializable> {

    List<T> find(int rowNum, int count);

    List<T> findAll();

    Integer save(T entity);

    void saveOrUpdateItem(T item);

    void updateItem(T item);

    void saveOrUpdateList(List<T> itemList);

    void saveList(List<T> itemList);

    int count();

    void delete(T item);

    void delete(List<T> entities);

    void deleteAll();
}
