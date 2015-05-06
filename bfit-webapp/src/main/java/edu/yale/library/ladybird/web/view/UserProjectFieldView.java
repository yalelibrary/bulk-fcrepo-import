package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.UserProjectField;
import edu.yale.library.ladybird.persistence.dao.UserProjectFieldDAO;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Displays all field assignments.
 * @see AssignProjectFieldView for class that assigns values. Have kept classes separate to keep things straight.
 *
 * @author Osman Din
 */
@ManagedBean
@ViewScoped
public class UserProjectFieldView extends AbstractView {

    private List<UserProjectField> itemList;

    @Inject
    private UserProjectFieldDAO userProjectFieldDAO;

    @PostConstruct
    public void init() {
        initFields();
        itemList = userProjectFieldDAO.findAll();
    }

    public List<UserProjectField> getItemList() {
        return itemList;
    }

    public void setItemList(List<UserProjectField> itemList) {
        this.itemList = itemList;
    }
}
