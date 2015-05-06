package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserPreferences;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface UserPreferencesDAO extends GenericDAO<UserPreferences, Integer> {

    List<UserPreferences> findByUserId(int userId);

}

