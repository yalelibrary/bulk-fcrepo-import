package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportJobContents;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ImportJobContentsDAO extends GenericDAO<ImportJobContents, Integer> {
    List<ImportJobContents> findByRow(int importId, int row);

    List<ImportJobContents> findByImportId(final int arg);

    int getNumEntriesPerImportJob(final int arg);

    int getNumRowsPerImportJob(final int arg);
}

