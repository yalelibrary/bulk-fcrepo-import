package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.Monitor;

import java.util.List;

public interface MonitorDAO extends GenericDAO<Monitor, Integer> {

    List<Monitor> findByUserAndProject(int userId, int projectId);

}

