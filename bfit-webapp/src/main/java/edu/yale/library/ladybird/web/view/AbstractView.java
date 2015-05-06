package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.persistence.dao.GenericDAO;
import org.omnifaces.util.Faces;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class AbstractView<T> {
    protected GenericDAO<T, Integer> dao;

    public AbstractView() {
    }

    public String find() {
        return dao.findAll().toString();
    }

    public void initFields() {
        DaoInitializer.injectFields(this);
    }

    public String ok() {
        return NavigationCase.OK.toString();
    }

    public String fail() {
        return NavigationCase.FAIL.toString();
    }

    public boolean isParamNull(String param) {
        return Faces.getRequestParameter(param) == null;
    }

    public boolean isParamEmpty(String param) {
        return Faces.getRequestParameter(param).isEmpty();
    }
}