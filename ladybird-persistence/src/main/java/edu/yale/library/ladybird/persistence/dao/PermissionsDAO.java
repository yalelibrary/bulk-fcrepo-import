package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Permissions;

public interface PermissionsDAO extends GenericDAO<Permissions, Integer> {

    Permissions findById(int permissionsId);

    Permissions findByName(String label);

}

