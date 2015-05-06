package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Settings;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface SettingsDAO extends GenericDAO<Settings, Integer> {

    Settings findById(int rowId);

    Settings findByProperty(String property);

}

