package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ImportJobNotifications;

import java.util.List;

public interface ImportJobNotificationsDAO extends GenericDAO<ImportJobNotifications, Integer> {

    List<ImportJobNotifications> findByUserAndJobId(int userId, int jobId);

    List<ImportJobNotifications> findAllUnsent();

}

