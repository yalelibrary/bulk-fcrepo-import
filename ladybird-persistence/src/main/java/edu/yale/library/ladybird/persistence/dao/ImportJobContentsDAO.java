package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportJobContents;

import java.util.List;

public interface ImportJobContentsDAO extends GenericDAO<ImportJobContents, Integer> {
    List<ImportJobContents> findByRow(final int arg);

    List<ImportJobContents> findByImportId(final int arg);

    int getNumEntriesPerImportJob(final int arg);

    int getNumRowsPerImportJob(final int arg);
}

