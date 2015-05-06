package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportJob;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ImportJobDAO extends GenericDAO<ImportJob, Integer> {

    List<ImportJob> findByUser(int userId);

    List<ImportJob> findByJobId(int jobId);

    List<ImportJob> findByRequestId(int requestID);
}

