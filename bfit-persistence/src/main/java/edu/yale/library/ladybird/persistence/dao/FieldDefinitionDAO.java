package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.FieldDefinition;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface FieldDefinitionDAO extends GenericDAO<FieldDefinition, Integer> {

    FieldDefinition findByFdid(int fdid);

}

