package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportJobExhead;

import java.util.List;

public interface ImportJobExheadDAO extends GenericDAO<ImportJobExhead, Integer> {
    List<ImportJobExhead> findByImportId(final int arg);

    int getNumEntriesPerImportJob(final int arg);

}

