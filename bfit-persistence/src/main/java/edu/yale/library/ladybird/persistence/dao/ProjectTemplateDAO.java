package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.ProjectTemplate;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface ProjectTemplateDAO extends GenericDAO<ProjectTemplate, Integer> {

    int getCountByLabel(String arg);

    List<ProjectTemplate> findByProjectId(int projectId);

    ProjectTemplate findByLabel(String label);

}

