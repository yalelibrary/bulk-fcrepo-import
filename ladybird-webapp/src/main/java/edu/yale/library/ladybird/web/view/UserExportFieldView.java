
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.entity.UserExportField;
import edu.yale.library.ladybird.persistence.dao.UserExportFieldDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@RequestScoped
public class UserExportFieldView extends AbstractView {
    private final Logger logger = getLogger(this.getClass());

    private List<UserExportField> itemList = new ArrayList<>();

    @Inject
    private UserExportFieldDAO userExportFieldDAO;

    @PostConstruct
    public void init() {
        initFields();
    }

    public List<UserExportField> getItemList() {
        return itemList;
    }

    public void setItemList(List<UserExportField> itemList) {
        this.itemList = itemList;
    }

    //TODO
    public String save() {
        return NavigationCase.OK.toString();
    }

    //TODO
    public String remove(UserExportField item) {
        return NavigationCase.OK.toString();
    }

}


