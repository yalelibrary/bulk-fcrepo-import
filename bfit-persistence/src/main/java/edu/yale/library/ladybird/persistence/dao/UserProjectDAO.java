package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserProject;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface UserProjectDAO extends GenericDAO<UserProject, Integer> {

    List<UserProject> findByProjectId(int projectId);

    List<UserProject> findByUserId(int userId);

    List<UserProject> findByUserAndProject(int userId, int projectId);
}

