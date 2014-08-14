package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.User;
import edu.yale.library.ladybird.persistence.dao.UserDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserHibernateDAO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

/**
 * Used for lazy loading paginated view
 */
public class UserDataModel extends LazyDataModel<User> {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDataModel.class);

    @Override
    public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        final UserDAO userDAO = new UserHibernateDAO(); //TODO
        try {
            final List<User> list = userDAO.find(first, first + pageSize);
            this.setRowCount(list.size());
            return list;
        } catch (Throwable e) {
            logger.error("Error lazy loading results", e);
            throw e;
        }
    }

    public UserDataModel() {
    }
}