package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Settings;

public interface SettingsDAO extends GenericDAO<Settings, Integer> {

    Settings findById(int rowId);

    Settings findByProperty(String property);

}

