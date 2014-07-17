package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserProjectFieldExportOptions;

public interface UserProjectFieldExportOptionsDAO extends GenericDAO<UserProjectFieldExportOptions, Integer> {

    UserProjectFieldExportOptions findByUserAndProjectAndFdid(int userId, int projectId, int fdid);

}

