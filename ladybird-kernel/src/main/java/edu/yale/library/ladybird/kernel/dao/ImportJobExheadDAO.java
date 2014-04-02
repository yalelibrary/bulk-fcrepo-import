

package edu.yale.library.ladybird.kernel.dao;

import edu.yale.library.ladybird.kernel.beans.ImportJobExhead;

import java.util.List;

public interface ImportJobExheadDAO extends GenericDAO<ImportJobExhead, Integer> {
    List<ImportJobExhead> findByImportId(final int arg);

    int getNumEntriesPerImportJob(final int arg);

}

