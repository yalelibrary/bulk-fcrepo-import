package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.FieldMarcMapping;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface FieldMarcMappingDAO extends GenericDAO<FieldMarcMapping, Integer> {

    FieldMarcMapping findByFdid(int fdid);

}

