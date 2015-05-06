package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserProjectFieldExportOptions;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface UserProjectFieldExportOptionsDAO extends GenericDAO<UserProjectFieldExportOptions, Integer> {

    UserProjectFieldExportOptions findByUserAndProjectAndFdid(int userId, int projectId, int fdid);

}

