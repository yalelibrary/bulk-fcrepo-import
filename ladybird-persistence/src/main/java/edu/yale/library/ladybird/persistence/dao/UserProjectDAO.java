package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserProject;

import java.util.List;

public interface UserProjectDAO extends GenericDAO<UserProject, Integer> {

    List<UserProject> findByProjectId(int projectId);

}

