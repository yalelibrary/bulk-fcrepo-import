

package edu.yale.library.dao;

import edu.yale.library.beans.ImportJobContents;
import edu.yale.library.beans.ImportJobExhead;

import java.util.List;

public interface ImportJobExheadDAO extends GenericDAO<ImportJobExhead, Integer>
{
    public List<ImportJobContents> findByImportId(final int arg);

    public int getNumEntriesPerImportJob(final int arg);

}

