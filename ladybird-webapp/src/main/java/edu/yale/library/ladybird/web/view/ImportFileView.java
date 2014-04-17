
package edu.yale.library.ladybird.web.view;


import edu.yale.library.ladybird.kernel.beans.ImportFile;
import edu.yale.library.ladybird.persistence.dao.ImportFileDAO;
import org.slf4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean (name = "ImportFileView")
@RequestScoped
public class ImportFileView extends AbstractView
{
    private final Logger logger = getLogger(this.getClass());

    private List<ImportFile> itemList;

    @Inject
    private ImportFileDAO entityDAO;

    @PostConstruct
    public void init()
    {
       initFields();
       dao = entityDAO;
    }

    public List<ImportFile> getItemList() {
        return itemList;
    }

    public void setItemList(List<ImportFile> itemList) {
        this.itemList = itemList;
    }
}


