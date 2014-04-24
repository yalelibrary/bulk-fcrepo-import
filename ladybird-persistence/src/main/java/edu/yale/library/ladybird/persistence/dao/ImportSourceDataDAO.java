

package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.entity.model.ImportSourceData;

import java.util.List;

public interface ImportSourceDataDAO extends GenericDAO<ImportSourceData, Integer> {

    List<ImportSourceData> findByImportId(int importId);

    List<ImportSourceData> findByImportId(int importId, int rowNum);


}

