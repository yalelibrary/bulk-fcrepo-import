package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportSourceData;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ImportSourceDataDAO extends GenericDAO<ImportSourceData, Integer> {

    List<ImportSourceData> findByImportId(int importId);

    List<ImportSourceData> findByImportIdAndLocalIdentifier(int importId, String localIdentifier); //e.g. bibid


}

