package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserPreferences;

import java.util.List;

public interface UserPreferencesDAO extends GenericDAO<UserPreferences, Integer> {

    List<UserPreferences> findByUserId(int userId);

}

