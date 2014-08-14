package edu.yale.library.ladybird.persistence.dao;


import edu.yale.library.ladybird.entity.Roles;

public interface RolesDAO extends GenericDAO<Roles, Integer> {

    Roles findById(int roleId);

}

