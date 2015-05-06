package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.UserProjectField;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface UserProjectFieldDAO extends GenericDAO<UserProjectField, Integer> {

    List<UserProjectField> findByUserAndProject(int userId, int projectId);

}

