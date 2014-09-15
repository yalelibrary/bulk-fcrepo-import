package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.ObjectFile;
import edu.yale.library.ladybird.persistence.dao.ObjectFileDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.ObjectFileHibernateDAO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;

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
        logger.trace("Looking for paged results. Param1={} Param2={}", first, pageSize);
        final ObjectFileDAO dao = new ObjectFileHibernateDAO();
        AuthUtil authUtil = new AuthUtil();

        try {
            final int currentProjectId = authUtil.getDefaultProjectForCurrentUser().getProjectId();
            logger.trace("Current projectId={}", currentProjectId);

            int dataSize = dao.count();
            this.setRowCount(dataSize);

            if (dataSize > pageSize) {
                return dao.findByProjectMax(currentProjectId, first, first + pageSize);
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