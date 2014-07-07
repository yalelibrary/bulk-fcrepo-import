package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportJob;

import java.util.List;

public interface ImportJobDAO extends GenericDAO<ImportJob, Integer> {

    List<ImportJob> findByUser(int userId);

}

