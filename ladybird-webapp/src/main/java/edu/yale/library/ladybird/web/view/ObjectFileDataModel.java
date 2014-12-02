package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.entity.Project;
import edu.yale.library.ladybird.persistence.dao.ObjectDAO;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectHibernateDAO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Used for lazy loading paginated view
 */
public class ObjectFileDataModel extends LazyDataModel<ObjectFile> {

    private static final Logger logger = getLogger(ObjectFileDataModel.class);

    @Override
    public List<ObjectFile> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        //logger.trace("Looking for paged results. Param1={} Param2={}", first, pageSize);
        final ObjectFileDAO dao = new ObjectFileHibernateDAO();
        AuthUtil authUtil = new AuthUtil();

        //logger.trace("ObjectFile count={}", dao.count());

        final ObjectDAO objectDAO = new ObjectHibernateDAO();

        final Project currentProject = authUtil.getDefaultProjectForCurrentUser();

        if (currentProject == null) {
            return Collections.emptyList();
        }


        final int dataSize = objectDAO.projectCount(currentProject.getProjectId());

        try {
            final int currentProjectId = authUtil.getDefaultProjectForCurrentUser().getProjectId();

            //logger.trace("Current projectId={}", currentProjectId);
            //logger.trace("Total num. of objects is={}", dataSize);

            this.setRowCount(dataSize);

            if (dataSize > pageSize) {
                return dao.findByProjectMax(currentProjectId, first, pageSize);
            } else {
                return dao.findByProject(currentProjectId);
            }
        } catch (Throwable e) {
            logger.error("Error lazy loading results", e);
            throw e;
        }
    }

    public ObjectFileDataModel() {
    }
}