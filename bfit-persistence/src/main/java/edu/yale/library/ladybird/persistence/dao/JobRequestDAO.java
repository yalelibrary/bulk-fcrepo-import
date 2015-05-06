package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.JobRequest;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface JobRequestDAO extends GenericDAO<JobRequest, Integer> {

    List<JobRequest> findByUserAndProject(int userId, int projectId);

}

