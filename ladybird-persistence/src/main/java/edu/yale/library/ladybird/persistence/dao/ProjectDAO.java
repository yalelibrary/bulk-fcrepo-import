package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Project;

import java.util.List;

public interface ProjectDAO extends GenericDAO<Project, Integer> {

    public List<Project> findByLabel(String label);

}

