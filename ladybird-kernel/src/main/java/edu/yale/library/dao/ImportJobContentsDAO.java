

package edu.yale.library.dao;

import edu.yale.library.beans.ImportJobContents;

import java.util.List;

public interface ImportJobContentsDAO extends GenericDAO<ImportJobContents, Integer>
{
    public List<ImportJobContents> findByRow(final int arg);

    public List<ImportJobContents> findByImportId(final int arg);

    public int getNumEntriesPerImportJob(final int arg);

    public int getNumRowsPerImportJob(final int arg);
}

