package edu.yale.library.view;


import edu.yale.library.beans.Monitor;
import edu.yale.library.dao.MonitorDAO;
import edu.yale.library.engine.cron.FilePickerScheduler;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@ManagedBean
@ApplicationScoped
public class MonitorView extends AbstractView
{
    private final Logger logger = getLogger(this.getClass());

    private List<Monitor> itemList;

    Monitor item = new Monitor();

    @Inject
    private MonitorDAO monitorDAO;

    @PostConstruct
    public void init()
    {
        initFields();
        dao = monitorDAO;
    }

    public void save()
    {
        try
        {
            int itemId = dao.save(item);
            // now schedule it
            FilePickerScheduler filePickerScheduler = new FilePickerScheduler();
            //assumes a new jobscheduler per directory
            filePickerScheduler.schedulePickJob("pickup_job_" + itemId, //some uuid?
                    "trigger" + itemId, "group" + itemId, getCronString(), item.getDirPath());
        }
        catch (Exception e)
        {
            logger.error("Error saving item", e);
        }
    }

    @Override
    public String toString()
    {
        return item.toString();
    }

    //Read this?
    private String getCronString()
    {
        return "0/60 * * * * ?";
    }

    public List getItemList()
    {
        List<Monitor> monitorList =  dao.findAll();
        return monitorList;
    }

    public Monitor getItem()
    {
        return item;
    }

    public void setItem(Monitor item)
    {
        this.item = item;
    }


}


