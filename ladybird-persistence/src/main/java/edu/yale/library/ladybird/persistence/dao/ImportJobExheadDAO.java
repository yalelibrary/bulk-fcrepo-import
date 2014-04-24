

package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.entity.model.ImportJobExhead;

import java.util.List;

public interface ImportJobExheadDAO extends GenericDAO<ImportJobExhead, Integer> {
    List<ImportJobExhead> findByImportId(final int arg);

    int getNumEntriesPerImportJob(final int arg);

}

